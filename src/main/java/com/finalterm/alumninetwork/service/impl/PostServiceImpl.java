
package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalterm.alumninetwork.dto.response.FeedResponseDto;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.mapper.PostMapper;
import com.finalterm.alumninetwork.pojo.*;
import com.finalterm.alumninetwork.repository.PostImageRepository;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostImageService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import com.finalterm.alumninetwork.util.PostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
    private PostImageService postImageService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;


    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactionService reactionService;


    // Thời gian cache cho các post
    private final long POST_CACHE_TTL = 3600; // 1 giờ (tính bằng giây)

    @Autowired
    private ObjectMapper objectMapper;

    // --------------------------- ADMIN SERVICE --------------------------------------
    @Override
    @Transactional
    public List<PostDTO> getPosts() { //For admin -> get All Posts
        return this.postRepository.getAll().stream().map(post -> {
                int totalComments = this.commentService.getTotalCommentByPostId(post.getId());
                Map<String, Integer> stats = this.reactionService.statsReactionByPostId(post.getId());
                List<String> imageUrls = postImageService.getPostImagesByPostId(post.getId())
                    .stream()
                    .map(PostImage::getUrl)
                    .collect(Collectors.toList());
            return PostMapper.toPostDTO(post, totalComments, stats, imageUrls);
            }).collect(Collectors.toList());
    }

    @Override
    public List<Object[]> statisticPosts(String timeType, int year) {
        return this.postRepository.statisticPosts(timeType, year);
    }


    @Override
    public Post getPostById(int postId) {
        return this.postRepository.getPostById(postId);
    }

    // --------------------------- CILENT SERVICE --------------------------------------

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
            String postId = params.get("postId");
            p = this.postRepository.getPostById(Integer.parseInt(postId));

            //-> update Post thi cap nhap lai noi dung,....
            objectRedisTemplate.delete(PostUtil.generatePostKey(postId));
            if (p == null)
                throw new RuntimeException("Post not found");
        }

        String profileRedisKey = PostUtil.generatePostProfileKey(String.valueOf(user.getId()));
        String postKey = PostUtil.generatePostKey(String.valueOf(String.valueOf(p.getId())));
        String postCommentCountKey = PostUtil.generateTotalCommentCount(String.valueOf(String.valueOf(p.getId())));
        String postReactionCountKey = PostUtil.generatePostReactionStatsKey(String.valueOf(String.valueOf(p.getId())));
        String postImagesKey = PostUtil.generatePostImagesKey(String.valueOf(String.valueOf(p.getId())));

        p.setTitle(params.get("title"));
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

                        this.postImageService.saveOrUpdate(postImage);

                        p.getImages().add(postImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Error upload Cloudinary", e);
                    }

        // Cache lại
        int totalComments = isNew ? 0 : commentService.getTotalCommentByPostId(p.getId());
        Map<String, Integer> statsReaction = isNew
                ? reactionService.initStatsReaction()
                : reactionService.statsReactionByPostId(p.getId());

        List<String> imgUrls = p.getImages().stream().map(PostImage::getUrl).toList();

        if (isNew) {
            ZSetCacheNewPost(profileRedisKey, p);

            objectRedisTemplate.opsForValue().set(postKey, p);
            objectRedisTemplate.opsForValue().set(postCommentCountKey, totalComments);
            objectRedisTemplate.opsForHash().putAll(postReactionCountKey, statsReaction);


            if (imgUrls != null && !imgUrls.isEmpty()) {
                for (String url : imgUrls)
                    objectRedisTemplate.opsForList().leftPush(postImagesKey, url);
                    objectRedisTemplate.expire(postImagesKey, POST_CACHE_TTL, TimeUnit.MINUTES);
            }
            updateFeedScore(p);

            objectRedisTemplate.expire(postReactionCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);
            objectRedisTemplate.expire(postCommentCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);
            objectRedisTemplate.expire(postKey, POST_CACHE_TTL, TimeUnit.MINUTES);
        }
        return PostMapper.toPostDTO(p, totalComments, statsReaction, imgUrls);
    }

    private void ZSetCacheNewPost(String postKey, Post post) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.zAdd(
                    postKey.getBytes(),
                    post.getCreatedAt().getTime(),
                    String.valueOf(post.getId()).getBytes()
            );
            connection.expire(postKey.getBytes(), 1800);
            return null;
        });
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
    public void lockOrUnlockComments(Post post) {
        String postContentKey = PostUtil.generatePostKey(String.valueOf(post.getId()));
        post.setBlockedComment(!post.getBlockedComment());
        redisTemplate.delete(postContentKey);
        this.postRepository.saveOrUpdate(post);
    }

    @Override
    public void toggleReaction(Post post, User user, EnumReaction type) {
        ReactionDto reactionDto = this.reactionService.getReactionByPostIdAndUserId(post.getId(), user.getId());

        if (reactionDto == null || !type.equals(reactionDto.getType())) {
            this.reactionService.reactToPost(post.getId(), user,  type);
            return;
        }

        this.reactionService.removeReaction(post.getId(), user.getId());
    }

    //------------------------------------------- REDIS CACHE -----------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<PostDTO> getMyPosts(int userId, Date createdDate, int limit) {
        String userPostIdsKey = PostUtil.generatePostProfileKey(String.valueOf(userId));
        List<PostDTO> result = new ArrayList<>();

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

            if (myPosts.isEmpty()) {
                return Collections.emptyList();
            }

            // Cache danh sách ID
            cachePosts(userPostIdsKey, myPosts);

            // Tạo danh sách ID từ kết quả database
            postIds = myPosts.stream()
                    .map(post -> String.valueOf(post.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        for (String postIdStr : postIds) {
            int postId = Integer.parseInt(postIdStr);

            PostDTO postDTO = this.getPostByIdToCache(postId);
            if (postDTO != null) {
                result.add(postDTO);
            }
        }
        return result;
    }

    private PostDTO getPostByIdToCache(int id) {
        String postKey = PostUtil.generatePostKey(String.valueOf(id));
        String postCommentCountKey = PostUtil.generateTotalCommentCount(String.valueOf(id));
        String postReactionCountKey = PostUtil.generatePostReactionStatsKey(String.valueOf(id));
        String postImagesKey = PostUtil.generatePostImagesKey(String.valueOf(id));

        PostDTO postDTO = new PostDTO();
        if (!redisTemplate.hasKey(postKey)) {

            Post post = this.postRepository.getPostById(id);

            int commentCount = this.commentService.getTotalCommentByPostId(post.getId());
            objectRedisTemplate.opsForValue().set(postCommentCountKey, commentCount);
            objectRedisTemplate.expire(postCommentCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            Map<String, Integer> reactionStats = this.reactionService.statsReactionByPostId(post.getId());
            objectRedisTemplate.opsForHash().putAll(postReactionCountKey, reactionStats);
            objectRedisTemplate.expire(postReactionCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            List<String> imageUrls = this.postImageService.getPostImagesByPostId(post.getId())
                    .stream()
                    .map(PostImage::getUrl)
                    .collect(Collectors.toList());

            for (String url : imageUrls) {
                objectRedisTemplate.opsForList().leftPush(postImagesKey, url);
            }
            objectRedisTemplate.expire(postImagesKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            objectRedisTemplate.opsForValue().set(postKey, post);
            objectRedisTemplate.expire(postKey, POST_CACHE_TTL, TimeUnit.MINUTES);

            postDTO = PostMapper.toPostDTO(post, commentCount, reactionStats, imageUrls);
        } else {
            Object object = objectRedisTemplate.opsForValue().get(postKey);
            Post post = objectMapper.convertValue(object, Post.class);

            int commentCount = (int) objectRedisTemplate.opsForValue().get(postCommentCountKey);

            Map<Object, Object> reactionStats = objectRedisTemplate.opsForHash().entries(postReactionCountKey);
            Map<String, Integer> reactionStatMap = reactionStats.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> (String) entry.getKey(),
                            entry -> (Integer) entry.getValue()
                    ));
            List<Object> imageUrls = objectRedisTemplate.opsForList().range(postImagesKey, 0, -1);
            List<String> newImageUrls = imageUrls != null
                    ? imageUrls.stream().map(Object::toString).toList()
                    : Collections.emptyList();new ArrayList<>();
            if (post != null) {
                postDTO = PostMapper.toPostDTO(post, commentCount, reactionStatMap, newImageUrls);
            }
        }

        return postDTO;
    }

    private void cachePosts(String userPostIdsKey, List<Post> posts) {
        if (posts.isEmpty()) {
            return;
        }
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Post post : posts) {
                connection.zAdd(
                        userPostIdsKey.getBytes(),
                        post.getCreatedAt().getTime(),
                        String.valueOf(post.getId()).getBytes()
                );
            }
            connection.expire(userPostIdsKey.getBytes(), 300);
            return null;
        });
    }

    //Xóa cache Id của bài Post cá nhân
    private void invalidatePostListCache(int userId) {
        String userPostIdsKey = PostUtil.generatePostProfileKey(String.valueOf(userId));
        redisTemplate.delete(userPostIdsKey);
    }

    @Override
    @Transactional
    //Cache Feed sẽ lấy số lượng bài mới hoặc bài cũ nhưng có số lượng comment, reactions nhiều nhất
    //Lưu vào ZSet Redis theo score: createdDate + totalComment + totalReactions
    public FeedResponseDto loadGlobalFeed(Date cursorTime, int limit) {
        String feedKey = PostUtil.globalFeedKey();

        long maxScore = cursorTime != null ? cursorTime.getTime() - 1 : Long.MAX_VALUE;

        Set<ZSetOperations.TypedTuple<String>> postIdTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(feedKey, 0, maxScore, 0, limit);



        if (postIdTuples == null || postIdTuples.isEmpty()) {
            // Cache miss lấy post từ DB, nên đổi thành lấy một số lượng nhất định -> update sau
            List<Post> posts = this.postRepository.getAll();

            for (Post post : posts) {
                updateFeedScore(post);
            }

            postIdTuples = redisTemplate.opsForZSet()
                    .reverseRangeByScoreWithScores(feedKey, 0, maxScore, 0, limit);
        }


        List<PostDTO> result = new ArrayList<>();
        Long nextCursorTime = null;

        for (ZSetOperations.TypedTuple<String> tuple : postIdTuples) {
            int postId = Integer.parseInt(tuple.getValue());
            PostDTO dto = this.getPostByIdToCache(postId);
            if (dto != null) {
                result.add(dto);
            }
        }

        // Lấy score của phần tử cuối
        nextCursorTime = postIdTuples.stream()
                .map(ZSetOperations.TypedTuple::getScore)
                .min(Double::compare)
                .map(score -> (long)(score.doubleValue()))
                .orElse(null);

        System.out.println("Max Score: " + maxScore);
        System.out.println("Redis size: " + postIdTuples.size());

        return new FeedResponseDto(result, nextCursorTime);
    }

    public void updateFeedScore(Post post) {
        String feedKey = PostUtil.globalFeedKey();

        String postCommentCountKey = PostUtil.generateTotalCommentCount(String.valueOf(post.getId()));
        String postReactionCountKey = PostUtil.generatePostReactionStatsKey(String.valueOf(post.getId()));

        Integer commentCount = (Integer) objectRedisTemplate.opsForValue().get(postCommentCountKey);
        if (commentCount == null ) {
            commentCount = this.commentService.getTotalCommentByPostId(post.getId());
            objectRedisTemplate.opsForValue().set(postCommentCountKey, commentCount);
            objectRedisTemplate.expire(postCommentCountKey, POST_CACHE_TTL, TimeUnit.MINUTES);
        }

        int reactionCount;
        Map<Object, Object> reactionStats = objectRedisTemplate.opsForHash().entries(postReactionCountKey);
        Map<String, Integer> reactionStatMap;

        if (reactionStats == null || reactionStats.isEmpty()) {
            reactionStatMap = this.reactionService.statsReactionByPostId(post.getId());
        } else {
            reactionStatMap = reactionStats.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> (String) entry.getKey(),
                            entry -> (Integer) entry.getValue()
                    ));
        }

        reactionCount = reactionStatMap.getOrDefault("TOTAL", 0);

        long createdAtScore = post.getCreatedAt().getTime() / 100_000; // 1 điểm ~ 10 giây
        int interactionScore = commentCount * 2 + reactionCount; // giảm trọng số
        double finalScore = createdAtScore + interactionScore;

        System.out.printf("Post id: %d, score: %f\n", post.getId(), finalScore);

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.zAdd(
                    feedKey.getBytes(),
                    finalScore,
                    String.valueOf(post.getId()).getBytes()
            );
            return null;
        });
        redisTemplate.expire(feedKey, 5, TimeUnit.MINUTES);
    }
}
