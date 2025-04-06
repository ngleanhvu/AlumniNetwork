package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Comment;

import java.util.List;

public interface CommentRepository {
    void addComment(Comment comment);
    List<Comment> getCommentsByPostId();
}
