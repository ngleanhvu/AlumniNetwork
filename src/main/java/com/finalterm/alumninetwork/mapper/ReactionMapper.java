package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Reaction;

import java.util.Map;

public class ReactionMapper {
    public static ReactionDto toReactionDto(Reaction reaction) {
        ReactionDto dto = new ReactionDto();
        dto.setId(reaction.getId().toString());
        dto.setType(reaction.getType());
        dto.setPostId(reaction.getPost().getId());
        dto.setUserId(reaction.getUser().getId());
        dto.setCreatedDate(reaction.getCreatedDate().getTime());
        return dto;
    }

    public static ReactionDto mapReactionDataToDto(Map<Object, Object> reactionData) {
        ReactionDto reactionDto = new ReactionDto();
        reactionDto.setId((String) reactionData.get("id"));
        reactionDto.setType(EnumReaction.valueOf((String) reactionData.get("type")));
        reactionDto.setCreatedDate(Long.parseLong((String) reactionData.get("createdDate")));
        reactionDto.setUserId(Integer.parseInt((String) reactionData.get("userId")));
        reactionDto.setPostId(Integer.parseInt((String) reactionData.get("postId")));
        return reactionDto;
    }

}