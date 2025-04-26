package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.response.ReactionDto;

public interface ReactionConsumer {
    void saveReaction(ReactionDto reactionDto);
    void deleteReaction(int reactionId);
}
