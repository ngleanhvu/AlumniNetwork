package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Reaction;
import com.finalterm.alumninetwork.pojo.User;

import java.util.List;
import java.util.Map;

public interface ReactionService {
    void reactToPost(int postId, User user, EnumReaction type);
    void removeReaction(int postId, int reactionId);
    Map<String, Integer> statsReactionByPostId(int postId);
    List<ReactionDto> getTypeReactionByPostId(int postId, String type);
    Map<String, Integer> initStatsReaction();
}
