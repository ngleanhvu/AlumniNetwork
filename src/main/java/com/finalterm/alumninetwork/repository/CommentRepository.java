package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Comment;

import java.util.List;

public interface CommentRepository {

    Comment saveOrUpdate(Comment comment);
    Comment getCommentById(int id);
    List<Comment> getRootCommentsByPostId(int id);
    List<Comment> getCommentsByParentCommentId(int id);
    void deleteComment(int id);
}
