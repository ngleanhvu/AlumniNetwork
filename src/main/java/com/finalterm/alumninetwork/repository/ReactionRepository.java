package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Reaction;
import jakarta.persistence.NamedQuery;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository {
    Reaction addOrUpdateReaction(Reaction reaction);
    List<Reaction> getTypeReactionsByPostId(int postId, String type);
    void deleteReaction(int postId, int userId);
    long countTotalByPostId(int postId);
    long countByPostIdAndType(int postId, String type);
    Optional<Reaction> existsReaction(int postId, int userId);
}
