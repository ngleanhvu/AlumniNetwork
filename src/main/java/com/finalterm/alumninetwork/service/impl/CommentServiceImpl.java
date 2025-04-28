package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.exception.PostBlockedComment;
import com.finalterm.alumninetwork.mapper.CommentMapper;
import com.finalterm.alumninetwork.mapper.PostMapper;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import com.finalterm.alumninetwork.service.CommentService;
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

    @Override
    @Transactional
    public CommentDto addComment(Map<String, String> params, Post post, User user) {
        if (post.getBlockedComment())
            throw new PostBlockedComment("This Post is blocked");

        Comment comment = new Comment();
        comment.setCreatedAt(new Date());
        comment.setActive(true);
        comment.setContent(params.get("content"));
        comment.setUser(user);
        comment.setPost(post);
        comment.setReplies(new ArrayList<>());

        if (params.get("parentCommentId") != null) {
            int parentCommentId = Integer.parseInt(params.get("parentCommentId"));
            Comment parentComment = this.commentRepository.getCommentById(parentCommentId);
            if (parentComment != null) {
                comment.setParentCommentId(parentComment);
                Comment savedComment = this.commentRepository.saveOrUpdate(comment);

                //Update comment parent
                parentComment.getReplies().add(savedComment);
                this.commentRepository.saveOrUpdate(parentComment);

                String commentCountKey = "post:" + post.getId() + ":commentCount";
                redisTemplate.opsForValue().increment(commentCountKey, 1);

                //Xoa cache cu de sau khi them comment moi
                redisTemplate.delete("post:" + post.getId() + ":comments:" + parentCommentId + ":parentComments");

                return CommentMapper.toCommentDTO(savedComment);
            } else throw new RuntimeException("parentCommentId is null");
        } else {
            comment.setParentCommentId(null);
            this.commentRepository.saveOrUpdate(comment);

            String commentCountKey = "post:" + post.getId() + ":commentCount";
            redisTemplate.opsForValue().increment(commentCountKey);
            //Xoa cache cu de sau khi them comment moi
            redisTemplate.delete("post:" + post.getId() + ":rootComments");
            return CommentMapper.toCommentDTO(comment);
        }
    }

    @Override
    @Transactional
    public List<CommentDto> getPaginateComments(int postId, Date createdAt, int limit, Integer parentCommentId) {
        String commentRedisKey = "post:" + postId + (parentCommentId != null ?
                ":comments:" + parentCommentId + ":parentComments" :
                ":rootComments");

        double maxScore = createdAt != null ? createdAt.getTime() - 1 : Double.POSITIVE_INFINITY;
        Set<String> commentIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                commentRedisKey,
                0,
                maxScore,
                0,
                limit
        );

        List<Comment> comments;
        boolean ordered = true;

        //Cache miss
        if (commentIds == null || commentIds.isEmpty()) {
            comments = commentRepository.getPaginateComment(postId, createdAt, limit, parentCommentId);

            if (!comments.isEmpty())
                cacheComments(commentRedisKey, comments);
            else
                return Collections.emptyList();

        //Cache thanh cong!
        } else {
            //Chuyen tu set<integer> sang list<Integer>
            List<Integer> ids = commentIds.stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            comments = commentRepository.getCommentsByList(ids);
            ordered = false; //Can phai sap xep lai
        }

        // Chuyển đổi kết quả sang DTO
        if (ordered) {
            return comments.stream()
                    .map(CommentMapper::toCommentDTO)
                    .collect(Collectors.toList());
        } else {
            //Phai dua vao map de sap xep lai
            Map<Integer, Comment> commentMap = comments.stream()
                    .collect(Collectors.toMap(Comment::getId, Function.identity()));

            return commentIds.stream()
                    .map(Integer::valueOf)
                    .map(commentMap::get)
                    .filter(Objects::nonNull)
                    .map(CommentMapper::toCommentDTO)
                    .collect(Collectors.toList());
        }
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
            String commentCountKey = "post:" + postId + ":commentCount";

            Integer count = redisTemplate.opsForValue().decrement(commentCountKey).intValue();

            if (count < 0) {
                redisTemplate.opsForValue().set(commentCountKey, 0);
            }

            if (comment.getParentCommentId() != null) {
                redisTemplate.delete( "post:" + postId + ":comments:" + comment.getParentCommentId() + ":parentComments");
            }
            redisTemplate.delete( "post:" + postId + ":rootComments");
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
        String key = "post:" + postId + ":commentCount";

        Integer count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            count = commentRepository.countTotalCommentsByPostId(postId);
            redisTemplate.opsForValue().set(key, count, 30, TimeUnit.MINUTES);
        }
        return count;
    }


}