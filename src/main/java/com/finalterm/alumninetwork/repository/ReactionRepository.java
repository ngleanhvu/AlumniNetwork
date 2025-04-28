package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Reaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReactionRepository {
    Reaction addOrUpdateReaction(Reaction reaction);
    List<Reaction> getTypeReactionsByPostId(int postId, String type, int page);
    void deleteReaction(int postId, int userId);
    Integer countTotalByPostId(int postId);
    Integer countByPostIdAndType(int postId, String type);
    boolean existsReaction(int postId, int userId);
    Map<String, Integer> statsReactionByPostId(int postId);
    List<Reaction> getReactionsByPostId(int postId);
    void deleteReactionById(int reactionId);
    Reaction getReactionById(int reactionId);
    Reaction getReactionByPostIdAndUserId(int postId, int userId);
}
