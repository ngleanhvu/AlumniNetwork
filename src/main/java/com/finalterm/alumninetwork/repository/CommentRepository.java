package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Comment;

import java.util.Date;
import java.util.List;

public interface CommentRepository {

    Comment saveOrUpdate(Comment comment);
    Comment getCommentById(int id);
    List<Comment> getPaginateComment(int postId, Date createdAt, int limit, Integer parentCommentId);
    List<Comment> getCommentsByList(List<Integer> commentIds);
    Comment getAllLazyRelationsById(int id);
    void deleteComment(int id);
    int countTotalCommentsByPostId(int id);
    List<Comment> getCommentsByParentCommentId(Comment parentComment);
}
