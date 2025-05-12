package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CommentMapper {

    @Autowired
    private CommentRepository commentRepository;

    public static CommentDto toCommentDTO(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setParentCommentId(comment.getParentCommentId() != null ? comment.getParentCommentId().getId() : 0);
        commentDto.setUser(UserWithPostMapper.toUserWithPostDto(comment.getUser()));
        commentDto.setPostOwnerId(comment.getPost().getUser().getId());

        int repliesCount = Optional.ofNullable(comment.getReplies())
                .map(List::size)
                .orElse(0);

        commentDto.setCountReplies(repliesCount);

        return commentDto;
    }
}
