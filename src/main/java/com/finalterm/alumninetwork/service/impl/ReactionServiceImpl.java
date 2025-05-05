package com.finalterm.alumninetwork.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.finalterm.alumninetwork.util.PostUtil;
import com.finalterm.alumninetwork.util.ReactionUtil;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    RedisTemplate<String, Integer> integerRedisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public void reactToPost(int postId, User user, EnumReaction type) {
        //        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(postId, type.name(), 1);
        //        String hashReactionKey = ReactionUtil.generateReactionHashKey(temporaryReactionId);
        String postReactionStatsType = PostUtil.generatePostReactionStatsKey(String.valueOf(postId));
        String postReactionUserKey = ReactionUtil.generateReactionByPostIdAndUserIdKey(postId, user.getId());

        String reactionTypeKey = type.name();

        Reaction existingReaction = this.reactionRepository.getReactionByPostIdAndUserId(postId, user.getId());

        if (existingReaction == null) { //Neu khong ton tai -> tao moi
            sendReactionToRabbitMQ(postId, user, type, null);
            if (redisTemplate.opsForHash().hasKey(postReactionStatsType, reactionTypeKey)) {
                redisTemplate.opsForHash().increment(postReactionStatsType, reactionTypeKey, 1);
                redisTemplate.opsForHash().increment(postReactionStatsType, "TOTAL", 1);
            }
        } else {
            //Neu ton tai. roi` thi update TYPE (trong 'toggle' se xu ly phan` remove)
            redisTemplate.delete(postReactionUserKey);

            sendReactionToRabbitMQ(postId, user, type, existingReaction);

            redisTemplate.opsForHash().increment(postReactionStatsType, existingReaction.getType().name(), -1);
            redisTemplate.opsForHash().increment(postReactionStatsType, reactionTypeKey, 1);
        }
    }

    private void sendReactionToRabbitMQ(int postId, User user, EnumReaction type, Reaction existingReaction) {
        String temporaryReactionId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        ReactionDto reactionDto = new ReactionDto();
        reactionDto.setPostId(postId);
        reactionDto.setUsername(user.getUsername());
        reactionDto.setUserId(user.getId());
        reactionDto.setType(type);

        if (existingReaction != null) {
            reactionDto.setId(String.valueOf(existingReaction.getId()));
            reactionDto.setCreatedDate(existingReaction.getCreatedDate().getTime());
        } else {
            reactionDto.setId(temporaryReactionId);
            reactionDto.setCreatedDate(timestamp);
        }

        rabbitTemplate.convertAndSend(Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.save")),
                reactionDto);
    }

    @Transactional
    @Override
    public void removeReaction(int postId, int userId) {
        Reaction reaction = this.reactionRepository.getReactionByPostIdAndUserId(postId, userId);
        String type = reaction.getType().name();
        Integer reactionId = reaction.getId();
        String postReactionStatsKey = PostUtil.generatePostReactionStatsKey(String.valueOf(postId));

        String postReactionUserKey = ReactionUtil.generateReactionByPostIdAndUserIdKey(postId,userId);
        redisTemplate.delete(postReactionUserKey);

        if (redisTemplate.opsForHash().hasKey(postReactionStatsKey, type)) {
            Object currentTypeCountObj = redisTemplate.opsForHash().get(postReactionStatsKey, type);
            Object currentTotalCountObj = redisTemplate.opsForHash().get(postReactionStatsKey, "TOTAL");

            long currentTypeCount = currentTypeCountObj != null ? Integer.parseInt(currentTypeCountObj.toString()) : 0L;
            long currentTotalCount = currentTotalCountObj != null ? Integer.parseInt(currentTotalCountObj.toString()) : 0L;

            if (currentTypeCount > 0) {
                redisTemplate.opsForHash().increment(postReactionStatsKey, type, -1);
            }
            if (currentTotalCount > 0) {
                redisTemplate.opsForHash().increment(postReactionStatsKey, "TOTAL", -1);
            }
        }

        rabbitTemplate.convertAndSend(Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.delete")),
                reactionId);
    }

    @Override
    @Transactional
    public Map<String, Integer> statsReactionByPostId(int postId) {
        String key = PostUtil.generatePostReactionStatsKey(String.valueOf(postId));
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);

        if (map == null || map.isEmpty()) {
            Map<String, Integer> stats = this.reactionRepository.statsReactionByPostId(postId);
            redisTemplate.opsForHash().putAll(key, stats);
            return stats;
        }

        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> ((Number) e.getValue()).intValue()
                ));
    }

    @Override
    public Map<String, Integer> initStatsReaction() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("LIKE", 0);
        stats.put("LOVE", 0);
        stats.put("HAHA", 0);
        stats.put("WOW", 0);
        stats.put("SAD", 0);
        stats.put("TOTAL", 0);
        return stats;
    }

    @Override
    public ReactionDto getReactionByPostIdAndUserId(int postId, int userId) {
        String postReactionUserKey = ReactionUtil.generateReactionByPostIdAndUserIdKey(postId, userId);

        ReactionDto reactionDto;
        if (!redisTemplate.hasKey(postReactionUserKey)) {
            Reaction reaction = this.reactionRepository.getReactionByPostIdAndUserId(postId, userId);
            if (reaction == null) {
                return null;
            }
            reactionDto = ReactionMapper.toReactionDto(reaction);
            redisTemplate.opsForValue().set(postReactionUserKey, reactionDto, 1, TimeUnit.MINUTES);
        } else {
            Object obj = redisTemplate.opsForValue().get(postReactionUserKey);
            reactionDto = objectMapper.convertValue(obj, ReactionDto.class);
        }
        return reactionDto;
    }

    @Override
    public List<ReactionDto> getTypeReactionByPostId(int postId, String type, int page) {
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(postId, type, page);

        int pageSize = Optional.ofNullable(env.getProperty("PAGE_SIZE_REACTION", Integer.class)).orElse(6);
        int start = (page - 1) * pageSize;
        int end = start + pageSize;

        Set<String> reactionKeys = redisTemplate.opsForZSet().reverseRange(zSetPostKey, start, end);
        List<ReactionDto> reactionDtos = new ArrayList<>();

        if (reactionKeys != null && !reactionKeys.isEmpty()) {
            for (String hashReactionKey : reactionKeys) {
                Map<Object, Object> reactionData = redisTemplate.opsForHash().entries(hashReactionKey);
                if (reactionData != null && !reactionData.isEmpty()) {
                    ReactionDto reactionDto = ReactionMapper.mapReactionDataToDto(reactionData);
                    reactionDtos.add(reactionDto);
                }
            }
        } else {
            reactionDtos = loadReactionsFromDbAndCache(postId, type, page);
        }

        redisTemplate.expire(zSetPostKey, 5, TimeUnit.SECONDS);
        return reactionDtos;
    }

    private List<ReactionDto> loadReactionsFromDbAndCache(int postId, String type, int page) {
        List<Reaction> reactions = reactionRepository.getTypeReactionsByPostId(postId, type, page);
        List<ReactionDto> reactionDtos = new ArrayList<>();

        for (Reaction reaction : reactions) {
            ReactionDto reactionDto = ReactionMapper.toReactionDto(reaction);
            reactionDtos.add(reactionDto);
            saveReactionToRedis(reactionDto);
        }

        return reactionDtos;
    }

    // Lưu reaction vào Redis
    private void saveReactionToRedis(ReactionDto reactionDto) {
        String hashReactionKey = ReactionUtil.generateReactionHashKey(reactionDto.getId());
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(reactionDto.getPostId(), reactionDto.getType().name(), 1);
        long timestamp = reactionDto.getCreatedDate();

        Map<String, String> fields = new HashMap<>();
        fields.put("id", reactionDto.getId());
        fields.put("type", reactionDto.getType().name());
        fields.put("createdDate", String.valueOf(timestamp));
        fields.put("userId", String.valueOf(reactionDto.getUserId()));
        fields.put("postId", String.valueOf(reactionDto.getPostId()));
        fields.put("username", reactionDto.getUsername());

        redisTemplate.opsForHash().putAll(hashReactionKey, fields);
        redisTemplate.expire(hashReactionKey, 5, TimeUnit.SECONDS);
        redisTemplate.opsForZSet().add(zSetPostKey, hashReactionKey, timestamp);
    }
}
