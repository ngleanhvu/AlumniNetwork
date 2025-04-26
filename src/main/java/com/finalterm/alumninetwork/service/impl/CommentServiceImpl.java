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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RedisTemplate redisTemplate;

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
                redisTemplate.opsForValue().increment(commentCountKey);

                //Xoa cache cu de sau khi them comment moi
                redisTemplate.delete("post:" + savedComment.getPost().getId() + ":comments");

                return CommentMapper.toCommentDTO(savedComment);
            } else throw new RuntimeException("parentCommentId is null");
        } else {
            comment.setParentCommentId(null);
            this.commentRepository.saveOrUpdate(comment);

            String commentCountKey = "post:" + post.getId() + ":commentCount";
            redisTemplate.opsForValue().increment(commentCountKey);
            //Xoa cache cu de sau khi them comment moi
            redisTemplate.delete("post:" + comment.getPost().getId() + ":comments");
            return CommentMapper.toCommentDTO(comment);
        }
    }

    @Override
    @Transactional
    public List<CommentDto> getRootCommentsByPostId(int postId) {
        String key = "post:" + postId + ":comments";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return (List<CommentDto>) cached;
        }

        List<CommentDto> listRootComments = this.commentRepository.getRootCommentsByPostId(postId)
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, listRootComments, 5, TimeUnit.MINUTES);
        return listRootComments;
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsByParentCommentId(int parentCommentId) {
        String key = "parentComments:" + parentCommentId + ":comments";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return (List<CommentDto>) cached;
        }

        List<CommentDto> listChildComments = this.commentRepository.getCommentsByParentCommentId(parentCommentId)
                .stream()
                .map(CommentMapper::toCommentDTO)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, listChildComments, 5, TimeUnit.MINUTES);
        return listChildComments;
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
            redisTemplate.delete("post:" + postId + ":comments");
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
        Integer count = (Integer) redisTemplate.opsForValue().get(key);
        if (count == null) {
            count = commentRepository.countTotalCommentsByPostId(postId);
            redisTemplate.opsForValue().set(key, count, 30, TimeUnit.MINUTES);
        }
        return count;
    }
}