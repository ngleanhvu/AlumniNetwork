package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CommentService {
    CommentDto addComment(Map<String, String> params, Post post, User user);
    List<CommentDto> getPaginateComments(int postId, Date createdAt, int limit, Integer parentCommentId);

    void deleteComment(int commentId);
    Comment getCommentById(int commentId);
    CommentDto updateComment(Comment comment);
    Integer getTotalCommentByPostId(int postId);
}
