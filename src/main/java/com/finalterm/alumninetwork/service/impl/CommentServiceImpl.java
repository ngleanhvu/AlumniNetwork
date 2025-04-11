package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.CommentDto;
import com.finalterm.alumninetwork.mapper.CommentMapper;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.CommentRepository;
import com.finalterm.alumninetwork.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto addComment(Map<String, String> params, Post post, User user) {
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
                return CommentMapper.toCommentDTO(savedComment);
            } else throw new RuntimeException("parentCommentId is null");
        } else {
            comment.setParentCommentId(null);
            this.commentRepository.saveOrUpdate(comment);
            return CommentMapper.toCommentDTO(comment);
        }
    }

    @Override
    @Transactional
    public List<CommentDto> getRootCommentsByPostId(int postId) {
        return this.commentRepository.getRootCommentsByPostId(postId).stream().map(CommentMapper::toCommentDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsByParentCommentId(int parentCommentId) {
        return this.commentRepository.getCommentsByParentCommentId(parentCommentId).stream().map(CommentMapper::toCommentDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(int commentId) {
        this.commentRepository.deleteComment(commentId);
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
}