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

        parentComment.getReplies().add(savedComment);
        commentRepository.saveOrUpdate(parentComment);

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
//        //Cache thanh cong!
//        } else {
//            //Chuyen tu set<integer> sang list<Integer>
//            List<Integer> ids = commentIds.stream()
//                    .map(Integer::valueOf)
//                    .collect(Collectors.toList());
//
//            comments = commentRepository.getCommentsByList(ids);
//            ordered = false; //Can phai sap xep lai
//        }
//
//        // Chuyển đổi kết quả sang DTO
//        if (ordered) {
//            return comments.stream()
//                    .map(CommentMapper::toCommentDTO)
//                    .collect(Collectors.toList());
//        } else {
//            //Phai dua vao map de sap xep lai
//            Map<Integer, Comment> commentMap = comments.stream()
//                    .collect(Collectors.toMap(Comment::getId, Function.identity()));
//
//            return commentIds.stream()
//                    .map(Integer::valueOf)
//                    .map(commentMap::get)
//                    .filter(Objects::nonNull)
//                    .map(CommentMapper::toCommentDTO)
//                    .collect(Collectors.toList());


    private CommentDto getCommentByIdToCache(int commentId) {
        String commentContentKey= CommentUtil.generateCommentContentKey(String.valueOf(commentId));

        CommentDto commentDto = new CommentDto();
        if (!redisTemplate.hasKey(commentContentKey)) { //Cache miss -> get from DB
            Comment comment = this.commentRepository.getCommentById(commentId);

            objectRedisTemplate.opsForValue().set(commentContentKey, comment);
            objectRedisTemplate.expire(commentContentKey, 15, TimeUnit.MINUTES);

            commentDto = CommentMapper.toCommentDTO(comment);
        } else { //get content from cache
            Object obj = objectRedisTemplate.opsForValue().get(commentContentKey);
            Comment c = objectMapper.convertValue(obj, Comment.class);

            if (c != null) {
                commentDto = CommentMapper.toCommentDTO(c);
            }
        }
        return commentDto;
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
            Integer count;

            if (totalChildComment == 0)
                count = redisTemplate.opsForValue().decrement(commentCountKey, 1).intValue();

            if (comment.getParentCommentId() != null) {
                redisTemplate.delete(CommentUtil.generateChildrenComment(String.valueOf(commentId), String.valueOf(comment.getParentCommentId())));
            }
            redisTemplate.delete(CommentUtil.generateRootComment(String.valueOf(commentId)));
            redisTemplate.delete(commentCountKey);
        }
    }

    @Transactional
    @Override
    public Comment getCommentById(int commentId) {
        return this.commentRepository.getCommentById(commentId);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Comment comment) {
        return CommentMapper.toCommentDTO(this.commentRepository.saveOrUpdate(comment));
    }

    @Override
    public Integer getTotalCommentByPostId(int postId) {
        String key = CommentUtil.generateTotalCommentCount(String.valueOf(postId));

        Integer count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            count = commentRepository.countTotalCommentsByPostId(postId);
            redisTemplate.opsForValue().set(key, count, 30, TimeUnit.MINUTES);
        }
        return count;
    }


}