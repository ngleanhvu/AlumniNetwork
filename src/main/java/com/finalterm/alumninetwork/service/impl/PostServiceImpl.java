package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.mapper.PostMapper;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.PostImage;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostImageRepository postImageRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactionService reactionService;


    // Thời gian cache cho các post
    private final long POST_CACHE_TTL = 3600; // 1 giờ (tính bằng giây)

    // --------------------------- ADMIN SERVICE --------------------------------------
    @Override
    @Transactional
    public List<PostDTO> getPosts() { //For admin -> get All Posts
        return this.postRepository.getAll().stream().map(post -> {
                int totalComments = this.commentService.getTotalCommentByPostId(post.getId());
                Map<String, Integer> stats = this.reactionService.statsReactionByPostId(post.getId());
                return PostMapper.toPostDTO(post, totalComments, stats);
            }).collect(Collectors.toList());
    }

    @Override
    public List<Object[]> statisticPosts(String timeType, int year) {
        return this.postRepository.statisticPosts(timeType, year);
    }

// --------------------------- CILENT SERVICE --------------------------------------
    @Override
    public Post getPostById(int id) {
        return this.postRepository.getPostById(id);
    }

    @Transactional
    @Override
    public PostDTO saveOrUpdate(Map<String, String> params, List<MultipartFile> fileImages, User user) {
        Post p;
        boolean isNew = params.get("postId") == null;

        if (isNew) {
            p = new Post();
            p.setCreatedAt(new Date());
            p.setUser(user);
        } else {
            int postId = Integer.parseInt(params.get("postId"));
            p = this.postRepository.getPostById(postId);
            if (p == null)
                throw new RuntimeException("Post not found");
        }

        p.setTitle(params.get("content"));
        this.postRepository.saveOrUpdate(p);

        // Upload ảnh nếu có
        if (fileImages != null && !fileImages.isEmpty())
            for (MultipartFile file : fileImages)
                if (!file.isEmpty())
                    try {
                        Map res = this.cloudinary.uploader().upload(file.getBytes(),
                                ObjectUtils.asMap("resource_type", "auto"));

                        PostImage postImage = new PostImage();
                        postImage.setUrl(res.get("url").toString());
                        postImage.setPost(p);

                        this.postImageRepository.saveOrUpdate(postImage);

                        p.getImages().add(postImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Error upload Cloudinary", e);
                    }



        // Cache lại
        int totalComments = isNew ? 0 : commentService.getTotalCommentByPostId(p.getId());
        Map<String, Integer> statsReaction = isNew
                ? reactionService.initStatsReaction()
                : reactionService.statsReactionByPostId(p.getId());

        cachePost(p, totalComments, statsReaction, user.getId());

        // Nếu là tạo mới, xóa cache danh sách
        if (isNew)
            invalidatePostListCache(user.getId());

        return PostMapper.toPostDTO(p, totalComments, statsReaction);
    }

    @Transactional
    @Override
    public void delete(int id) {
        Post p = this.postRepository.getPostById(id);
        if (p == null)
            throw new RuntimeException("Post not found");
        int userId = p.getUser().getId();

        invalidatePostListCache(userId);
        this.postRepository.delete(id);
    }


    @Transactional
    @Override
    public void lockComments(Post post) {
        post.setBlockedComment(true);
        invalidatePostListCache(post.getUser().getId());
        this.postRepository.saveOrUpdate(post);
    }

    //------------------------------------------- REDIS CACHE -----------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getMyPosts(int userId) {
        String postIdsKey = "user:postIds:" + userId;
        List<Integer> postIds = (List<Integer>) redisTemplate.opsForValue().get(postIdsKey);

        // Nếu chưa có danh sách postId cache -> lấy từ DB
        if (postIds == null) {
            postIds = postRepository.getMyPostIds(userId); // cần thêm hàm này
            redisTemplate.opsForValue().set(postIdsKey, postIds, 30, TimeUnit.MINUTES);
        }

        List<PostDTO> result = new ArrayList<>();
        for (Integer postId : postIds) {
            // Load post
            Post post = this.postRepository.getPostById(postId);

            // Load comment count
            Integer commentCount = (Integer) redisTemplate.opsForValue().get("post:" + postId + ":commentCount");
            if (commentCount == null) {
                commentCount = commentService.getTotalCommentByPostId(postId); // DB fallback
            }

            // Load reaction stats
            Map<Object, Object> cacheMap = redisTemplate.opsForHash().entries("post:" + postId + ":reactionStats");
            Map<String, Integer> reactionStats;
            if (cacheMap.isEmpty()) {
                reactionStats = reactionService.statsReactionByPostId(postId); // DB fallback
            } else {
                reactionStats = cacheMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> (String) e.getKey(),
                                e -> ((Number) e.getValue()).intValue()
                        ));
            }

            // Gom lại PostDTO
            PostDTO dto = PostMapper.toPostDTO(post, commentCount, reactionStats);
            result.add(dto);
        }

        return result;
    }


    //Save post to cache
    private void cachePost(Post post, Integer totalComments, Map<String, Integer> statsReaction, int userId) {

        String userPostIdsKey = "user:postIds:" + userId;
        // Lấy danh sách hiện tại từ cache (nếu có)
        List<Integer> userPostIds = (List<Integer>) redisTemplate.opsForValue().get(userPostIdsKey);

        if (userPostIds != null) {
            userPostIds.add(0, post.getId());
            redisTemplate.opsForValue().set(userPostIdsKey, userPostIds, 30, TimeUnit.MINUTES);
        }
    }

    //Xóa cache Id của bài Post cá nhân
    private void invalidatePostListCache(int userId) {
        String userPostIdsKey = "user:postIds:" + userId;
        redisTemplate.delete(userPostIdsKey);
    }
}
