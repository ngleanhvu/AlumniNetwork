package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.mapper.ReactionMapper;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.Reaction;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.PostRepository;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ReactionRepository reactionRepository;

    @Override
    @Transactional
    public ReactionDto reactToPost(int postId, int userId, EnumReaction type) {
        Post post = postService.getPostById(postId);
        User user = userService.findUserById(userId);

        if (post == null || user == null) {
            throw new RuntimeException("Post or User not found");
        }
        //Kiem tra da Reaction chua
        Optional<Reaction> existingReaction = reactionRepository.existsReaction(postId, userId);

        if (existingReaction.isPresent()) { //Update reaction
            Reaction reaction = existingReaction.get();
            reaction.setType(type);
            return ReactionMapper.toReactionDto(reactionRepository.addOrUpdateReaction(reaction));
        } else {// Tao reaction
            Reaction reaction = new Reaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setType(type);
            return ReactionMapper.toReactionDto(reactionRepository.addOrUpdateReaction(reaction));
        }
    }

    @Override
    @Transactional
    public void removeReaction(int postId, int userId) {
        this.reactionRepository.deleteReaction(postId, userId);
    }

    @Override
    @Transactional
    public Map<String, Long> statsReactionByPostId(int postId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("Like", reactionRepository.countByPostIdAndType(postId, EnumReaction.LIKE.name()));
        counts.put("Love", reactionRepository.countByPostIdAndType(postId, EnumReaction.LOVE.name()));
        counts.put("Haha", reactionRepository.countByPostIdAndType(postId, EnumReaction.HAHA.name()));
        counts.put("Total", reactionRepository.countTotalByPostId(postId));
        return counts;
    }

    @Override
    public List<ReactionDto> getTypeReactionByPostId(int postId, String type) {
        return this.reactionRepository.getTypeReactionsByPostId(postId, type)
                .stream()
                .map(ReactionMapper::toReactionDto)
                .collect(Collectors.toList());
    }
}
