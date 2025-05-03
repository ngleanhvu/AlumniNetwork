package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.pojo.Comment;

import java.util.List;
import java.util.Optional;

public class CommentMapper {
    public static CommentDto toCommentDTO(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        if (comment.getParentCommentId() != null)
            commentDto.setParentCommentId(comment.getParentCommentId().getId());
        commentDto.setUser(UserWithPostMapper.toUserWithPostDto(comment.getUser()));

        int repliesCount = Optional.ofNullable(comment.getReplies())
                .map(List::size)
                .orElse(0);
        
        commentDto.setCountReplies(repliesCount);
        return commentDto;
    }
}
