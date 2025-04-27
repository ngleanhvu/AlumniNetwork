package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.mapper.PostMapper;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.PostImage;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
    private RedisTemplate<String, String> redisTemplate;

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

        p.setTitle(params.get("title"));
        p.setContent(params.get("content"));

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

        // Nếu là tạo mới, xóa cache danh sách
        if (isNew) {
            String profileRedisKey = "profile" + user.getId() + ":user";
            redisTemplate.opsForZSet().add(profileRedisKey, String.valueOf(p.getId()), p.getCreatedAt().getTime());
        }

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


    // SỬA LẠI NẾU SẼ PHÂN TRANG THEO CURSOR được lấy từ Repository


    //................................... SAU ĐÓ ADD VÀO ZSET
    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getMyPosts(int userId, Date createdDate, int limit) {
        String userPostIdsKey = "profile" + userId + ":user";

        Set<String> postIds = redisTemplate.opsForZSet().reverseRangeByScore(
                userPostIdsKey,
                0,
                createdDate != null ? createdDate.getTime() - 1 : Double.POSITIVE_INFINITY,
                0,
                limit
        );

        //Cache miss
        if (postIds == null || postIds.isEmpty()) {
            List<Post> myPosts = postRepository.getPostPaginate(userId, createdDate, limit);

            if (myPosts.isEmpty())
                return Collections.emptyList();

            // Nạp vào Redis
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Post post : myPosts) {
                    connection.zAdd(
                            userPostIdsKey.getBytes(),
                            post.getCreatedAt().getTime(),
                            String.valueOf(post.getId()).getBytes()
                    );
                }
                connection.expire(userPostIdsKey.getBytes(), 3600);
                return null;
            });

            postIds = myPosts.stream()
                    .map(c -> String.valueOf(c.getId()))
                    .collect(Collectors.toSet());
        }
        List<PostDTO> result = new ArrayList<>();

        List<Integer> intPostIds = (postIds == null) ? Collections.emptyList() :
                postIds.stream().map(Integer::valueOf).toList();

        List<Post> posts = this.postRepository.getPostByPostIds(intPostIds);

        Map<Integer, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        for (Integer postId : intPostIds) {
            Post p = postMap.get(postId);
            if (p != null) {
                String commentCountStr = redisTemplate.opsForValue().get("post:" + p.getId() + ":commentCount");
                Integer commentCount;

                if (commentCountStr == null) {
                    commentCount = commentService.getTotalCommentByPostId(p.getId());
                } else {
                    commentCount = Integer.valueOf(commentCountStr);
                }

                // Load reaction stats
                Map<Object, Object> cacheMap = redisTemplate.opsForHash().entries("post:" + p.getId() + ":reactionStats");
                Map<String, Integer> reactionStats;
                if (cacheMap.isEmpty()) {
                    reactionStats = reactionService.statsReactionByPostId(p.getId()); // DB fallback
                } else {
                    reactionStats = cacheMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                    e -> String.valueOf(e.getKey()),
                                    e -> Integer.valueOf(e.getValue().toString())
                            ));
                }

                PostDTO dto = PostMapper.toPostDTO(p, commentCount, reactionStats);
                result.add(dto);
            }
        }
        return result;
    }

    //Xóa cache Id của bài Post cá nhân
    private void invalidatePostListCache(int userId) {
        String userPostIdsKey = "user:postIds:" + userId;
        redisTemplate.delete(userPostIdsKey);
    }
}
