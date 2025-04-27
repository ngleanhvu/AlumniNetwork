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
        String commentRedisKey = "post:" + postId + ":rootComments";

        if (parentCommentId != null) {
            commentRedisKey = "post:" + postId + ":comments:" + parentCommentId + ":parentComments";
        }

        Set<String> commentIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                commentRedisKey,
                0,
                createdAt != null ? createdAt.getTime() - 1 : Double.POSITIVE_INFINITY,
                0,
                limit
        );

        if (commentIds == null || commentIds.isEmpty()) {
            List<Comment> commentList = this.commentRepository.getPaginateComment(postId, createdAt, limit, parentCommentId);

            if (commentList.isEmpty())
                return Collections.emptyList();

            cacheComments(commentRedisKey, commentList);

            commentIds = commentList.stream()
                    .map(c -> String.valueOf(c.getId()))
                    .collect(Collectors.toSet());

//            //Cache lại
//            commentIds = stringRedisTemplate.opsForZSet().reverseRangeByScore(
//                    commentRedisKey,
//                    0,
//                    createdAt != null ? createdAt.getTime() - 1 : Double.POSITIVE_INFINITY,
//                    0,
//                    limit
//            );

        }

        List<Integer> commentIdList = commentIds.stream()
                .map(Integer::valueOf)
                .toList();


        List<Comment> comments = commentRepository.getCommentsByList(commentIdList);

        // Chuyển đổi sang DTO
        return comments.stream()
                .map(CommentMapper::toCommentDTO)
                .toList();
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