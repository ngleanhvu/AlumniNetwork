package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.dto.response.PostDTOV1;
import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.mapper.PostMapper;
import com.finalterm.alumninetwork.pojo.*;
import com.finalterm.alumninetwork.repository.CommentRepository;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import com.finalterm.alumninetwork.util.PostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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

    @Autowired
    private ReactionRepository reactionRepository;


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
    @Transactional
    @Override
    public PostDTOV1 getPostByIdV1(int id) {
        String postKey = PostUtil.generatePostKey(String.valueOf(id));
        String postCommentCountKey = PostUtil.generatePostCommentCountKey(String.valueOf(id));
        String postReactionCountKey = PostUtil.generatePostReactionStatsKey(String.valueOf(id));
        String postImagesKey = PostUtil.generatePostImagesKey(String.valueOf(id));
        PostDTOV1 postDTO = new PostDTOV1();
        if (!redisTemplate.hasKey(postKey)) {
            Post post = this.postRepository.getPostById(id);
            postDTO.setId(post.getId());
            postDTO.setTitle(post.getTitle());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setActive(post.getActive());
            postDTO.setBlockedComment(post.getBlockedComment());

            int commentCount = this.commentService.getTotalCommentByPostId(post.getId());
            redisTemplate.opsForValue().set(postCommentCountKey, commentCount);
            redisTemplate.expire(postCommentCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            Map<String, Integer> reactionStats = this.reactionService.statsReactionByPostId(post.getId());
            redisTemplate.opsForHash().putAll(postReactionCountKey, reactionStats);
            redisTemplate.expire(postReactionCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            List<String> imageUrls = this.postImageRepository.getPostImagesByPostId(post.getId())
                            .stream()
                            .map(PostImage::getUrl)
                            .collect(Collectors.toList());

            for (String url : imageUrls) {
                redisTemplate.opsForList().leftPush(postImagesKey, url);
            }
            redisTemplate.expire(postImagesKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set(postKey, post);
            redisTemplate.expire(postKey, POST_CACHE_TTL, TimeUnit.MINUTES);
            postDTO = PostMapper.toPostDTOV1(post, commentCount, reactionStats, imageUrls);

        } else {
            Post post = (Post) redisTemplate.opsForValue().get(postKey);
            int commentCount = (int) redisTemplate.opsForValue().get(postCommentCountKey);
            Map<Object, Object> reactionStats = redisTemplate.opsForHash().entries(postReactionCountKey);
            Map<String, Integer> reactionStatMap = reactionStats.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> (String) entry.getKey(),
                            entry -> (Integer) entry.getValue()
                    ));
            List<Object> imageUrls = redisTemplate.opsForList().range(postImagesKey, 0, -1);
            List<String> newImageUrls = imageUrls != null
                    ? imageUrls.stream().map(Object::toString).toList()
                    : Collections.emptyList();new ArrayList<>();
            if (post != null) {
                postDTO = PostMapper.toPostDTOV1(post, commentCount, reactionStatMap, newImageUrls);
            }
        }

        return postDTO;
    }

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
        p.setContent(params.get("content"));
        p.setActive(true);
        p.setBlockedComment(false);
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
            Map<Object, Object> cacheMap = redisTemplate.opsForHash().entries("post:" + postId + ":reaction:");
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

    @Override
    public void toggleReaction(Post post, User user, EnumReaction type) {
        ReactionDto reactionDto = this.reactionService.getReactionByPostIdAndUserId(post.getId(), user.getId());
        if (reactionDto == null) {
            this.reactionService.reactToPost(post.getId(), user,  type);
            return;
        }
        this.reactionService.removeReaction(post.getId(), user.getId());
    }
}

