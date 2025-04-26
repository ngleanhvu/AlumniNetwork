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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    ReactionRepository reactionRepository;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @Transactional
    public ReactionDto reactToPost(int postId, int userId, EnumReaction type) {
        Post post = postService.getPostById(postId);
        User user = userService.findUserById(userId);

        if (post == null || user == null) {
            throw new RuntimeException("Post or User not found");
        }
        //Kiem tra da Reaction chua -> Kiem tra key tren redis
        Optional<Reaction> existingReaction = reactionRepository.existsReaction(postId, userId);

        ReactionDto result;

        if (existingReaction.isPresent()) { //Update reaction
            Reaction reaction = existingReaction.get();
            reaction.setType(type);
            result = ReactionMapper.toReactionDto(reactionRepository.addOrUpdateReaction(reaction));
        } else {// Tao reaction
            Reaction reaction = new Reaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setType(type);
            result = ReactionMapper.toReactionDto(reactionRepository.addOrUpdateReaction(reaction));
        }

        // Xóa cache stats để đảm bảo lần sau sẽ lấy dữ liệu mới
        String keyReactionStats = "post:" + postId + ":reactionStats";
        String keyReactionPosts = "user:posts:" + post.getUser().getId();

        redisTemplate.delete(keyReactionStats);
        redisTemplate.delete(keyReactionPosts);

        return result;
    }

    @Override
    @Transactional
    public void removeReaction(int postId, int userId) {
        Post post = postService.getPostById(postId);
        this.reactionRepository.deleteReaction(postId, userId);

        String keyReactionStats = "post:" + postId + ":reactionStats";
        if (post != null) {
            String keyReactionPosts = "user:posts:" + post.getUser().getId();
            redisTemplate.delete(keyReactionPosts);
        }

        redisTemplate.delete(keyReactionStats);

    }

    @Override
    @Transactional
    public Map<String, Integer> statsReactionByPostId(int postId) {

        String key = "post:" + postId + ":reactionStats";
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

        if (map == null || map.isEmpty()) {
            Map<String, Integer> stats = this.reactionRepository.statsReactionByPostId(postId);

            redisTemplate.opsForHash().putAll(key, stats);
            redisTemplate.expire(key, 10, TimeUnit.MINUTES);

            return stats;
        }

        // convert Object to Map<String, Integer>
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> ((Number) e.getValue()).intValue()
                ));
    }

    @Override
    public Map<String, Integer> initStatsReaction() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Like", 0);
        stats.put("Love", 0);
        stats.put("Haha", 0);
        stats.put("Total", 0);
        return stats;
    }


    @Override
    public List<ReactionDto> getTypeReactionByPostId(int postId, String type) {
        return this.reactionRepository.getTypeReactionsByPostId(postId, type)
                .stream()
                .map(ReactionMapper::toReactionDto)
                .collect(Collectors.toList());
    }
}
