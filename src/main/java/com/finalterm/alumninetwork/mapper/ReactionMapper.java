package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.Reaction;

public class ReactionMapper {
    public static ReactionDto toReactionDto(Reaction reaction) {
        ReactionDto dto = new ReactionDto();
        dto.setId(reaction.getId());
        dto.setType(reaction.getType());
        dto.setPostId(reaction.getPost().getId());
        dto.setUserId(reaction.getUser().getId());
        dto.setFullName(reaction.getUser().getFullName());
        return dto;
    }
}
