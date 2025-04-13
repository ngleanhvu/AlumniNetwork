package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Reaction;

import java.util.List;
import java.util.Map;

public interface ReactionService {
    ReactionDto reactToPost(int postId, int userId, EnumReaction type);
    void removeReaction(int postId, int userId);
    Map<String, Long> statsReactionByPostId(int postId);
    List<ReactionDto> getTypeReactionByPostId(int postId, String type);
}
