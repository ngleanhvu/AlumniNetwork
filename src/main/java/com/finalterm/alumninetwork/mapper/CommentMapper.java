package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.pojo.Comment;

public class CommentMapper {
    public static CommentDto toCommentDTO(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        if (comment.getParentCommentId() != null)
            commentDto.setParentCommentId(comment.getParentCommentId().getId());
        commentDto.setUsername(comment.getUser().getFullName());
        commentDto.setCountReplies(comment.getReplies().size());
        return commentDto;
    }
}
