package com.finalterm.alumninetwork.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.exception.PostBlockedComment;
import com.finalterm.alumninetwork.mapper.CommentMapper;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.util.CommentUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    @Transactional
    public CommentDto updateComment(Map<String, String> params, Comment comment, User user) {
        String content = params.get("content");
        comment.setContent(content);

        //Cache lai noi dung comment
        String commentContentKey= CommentUtil.generateCommentContentKey(String.valueOf(comment.getId()));
        redisTemplate.delete(commentContentKey);

        Comment newComment = this.commentRepository.saveOrUpdate(comment);

        Comment updatedComment = this.commentRepository.getAllLazyRelationsById(newComment.getId());

        return CommentMapper.toCommentDTO(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto addComment(Map<String, String> params, Post post, User user) {
        if (post.getBlockedComment()) {
            throw new PostBlockedComment("This Post is blocked");
        }

        Comment comment = new Comment();
        comment.setCreatedAt(new Date());
        comment.setActive(true);
        comment.setContent(params.get("content"));
        comment.setUser(user);
        comment.setPost(post);
        comment.setReplies(new ArrayList<>());
        comment.setParentCommentId(null);

        String commentCountKey = CommentUtil.generateTotalCommentCount(String.valueOf(post.getId()));
        redisTemplate.opsForValue().increment(commentCountKey, 1);

        String parentCommentIdStr = params.get("parentCommentId");
        if (parentCommentIdStr != null) {

            return this.saveReplyComment(comment, Integer.parseInt(parentCommentIdStr));
        }
        return this.saveRootComment(comment);
    }


    private CommentDto saveReplyComment(Comment comment, int parentCommentId) {
        Comment parentComment = commentRepository.getCommentById(parentCommentId);
        if (parentComment == null) {
            throw new RuntimeException("parentCommentId is invalid");
        }

        comment.setParentCommentId(parentComment);
        Comment savedComment = commentRepository.saveOrUpdate(comment);

        //Cache lại số lượng replies của Comment cha
        String parentCommentContentKey = CommentUtil.generateCommentContentKey(String.valueOf(comment.getParentCommentId().getId()));
        redisTemplate.delete(parentCommentContentKey);

        List<Comment> replies = parentComment.getReplies();

        replies.add(savedComment);
        this.commentRepository.saveOrUpdate(parentComment);

        String childrenKey = CommentUtil.generateChildrenComment(
                String.valueOf(comment.getPost().getId()),
                String.valueOf(parentCommentId)
        );
        //Add to ZSet
        cacheNewComment(childrenKey, savedComment);

        return CommentMapper.toCommentDTO(savedComment);
    }

    private CommentDto saveRootComment(Comment comment) {
        Comment savedComment = commentRepository.saveOrUpdate(comment);

        String rootKey = CommentUtil.generateRootComment(String.valueOf(comment.getPost().getId()));

        cacheNewComment(rootKey, savedComment);

        return CommentMapper.toCommentDTO(savedComment);
    }

    @Override
    @Transactional
    public List<CommentDto> getPaginateComments(int postId, Date createdAt, int limit, Integer parentCommentId) {
        String commentRedisKey = parentCommentId != null ?
                CommentUtil.generateChildrenComment(String.valueOf(postId), String.valueOf(parentCommentId)) :
                CommentUtil.generateRootComment(String.valueOf(postId));

        double maxScore = createdAt != null ? createdAt.getTime() - 1 : Double.POSITIVE_INFINITY;
        Set<String> commentIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                commentRedisKey,
                0,
                maxScore,
                0,
                limit
        );

        List<Comment> comments;
        List<CommentDto> results = new ArrayList<>();

        //Cache miss
        if (commentIds == null || commentIds.isEmpty()) {
            comments = commentRepository.getPaginateComment(postId, createdAt, limit, parentCommentId);

            if (comments.isEmpty())
                return Collections.emptyList();

            cacheComments(commentRedisKey, comments);

            commentIds = comments.stream()
                    .map(comment -> String.valueOf(comment.getId()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        for (String commentIdStr : commentIds) {
            int commentId = Integer.parseInt(commentIdStr);

            CommentDto commentDto = this.getCommentByIdToCache(commentId);
            if (commentDto != null) {
                results.add(commentDto);
            }
        }
        return results;
    }


    private CommentDto getCommentByIdToCache(int commentId) {
        String commentContentKey= CommentUtil.generateCommentContentKey(String.valueOf(commentId));

        if (!redisTemplate.hasKey(commentContentKey)) { //Cache miss -> get from DB
            Comment comment = this.commentRepository.getCommentById(commentId);
            CommentDto cDto = CommentMapper.toCommentDTO(comment);
            objectRedisTemplate.opsForValue().set(commentContentKey, cDto);
            objectRedisTemplate.expire(commentContentKey, 2, TimeUnit.MINUTES);

            return cDto;
        } else { //get content from cache
            Object obj = objectRedisTemplate.opsForValue().get(commentContentKey);
            CommentDto c = objectMapper.convertValue(obj, CommentDto.class);

                return c;
        }
    }


    private void cacheNewComment(String commentIdsKey, Comment comment) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                connection.zAdd(
                        commentIdsKey.getBytes(),
                        comment.getCreatedAt().getTime(),
                        String.valueOf(comment.getId()).getBytes()
                );
            connection.expire(commentIdsKey.getBytes(), 1800);
            return null;
        });
    }

    private void cacheComments(String commentIdsKey, List<Comment> comments) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Comment comment : comments) {
                connection.zAdd(
                        commentIdsKey.getBytes(),
                        comment.getCreatedAt().getTime(),
                        String.valueOf(comment.getId()).getBytes()
                );
            }
            // Đặt TTL cho ZSet
            connection.expire(commentIdsKey.getBytes(), 1800);
            return null;
        });
    }

    @Override
    @Transactional
    public void deleteComment(int commentId) {
        Comment comment = this.commentRepository.getCommentById(commentId);

        if (comment != null) {
            int postId = comment.getPost().getId();
            this.commentRepository.deleteComment(commentId);
            String commentCountKey = CommentUtil.generateTotalCommentCount(String.valueOf(postId));
            Integer totalChildComment = comment.getReplies().size();

            if (totalChildComment == 0)
                redisTemplate.opsForValue().decrement(commentCountKey, 1).intValue();
            else{
                redisTemplate.delete(CommentUtil.generateChildrenComment(
                        String.valueOf(postId),
                        String.valueOf(commentId)));
                redisTemplate.delete(commentCountKey);
            }

            if (comment.getParentCommentId() != null) {
                redisTemplate.delete(CommentUtil.generateCommentContentKey(String.valueOf(comment.getParentCommentId())));
            } else {
                redisTemplate.delete(CommentUtil.generateRootComment(String.valueOf(postId)));
            }
            redisTemplate.delete(CommentUtil.generateCommentContentKey(String.valueOf(commentId)));
        }
    }

    @Transactional
    @Override
    public Comment getCommentById(int commentId) {
        return this.commentRepository.getCommentById(commentId);
    }


    @Override
    public Integer getTotalCommentByPostId(int postId) {
        String key = CommentUtil.generateTotalCommentCount(String.valueOf(postId));

        Integer count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            count = commentRepository.countTotalCommentsByPostId(postId);
            redisTemplate.opsForValue().set(key, count, 5, TimeUnit.MINUTES);
        }
        return count;
    }


}