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
import com.finalterm.alumninetwork.util.ReactionUtil;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
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
    RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Override
    @Transactional
    public void reactToPost(int postId, User user, EnumReaction type) {
        // Add fake id for reaction id
        String temporaryReactionId = UUID.randomUUID().toString();
        // Add current timestamp
        long timestamp = System.currentTimeMillis();
        // ZSet post key and Hash reaction key
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(postId, type.name());
        String hashReactionKey = ReactionUtil.generateReactionHashKey(temporaryReactionId);

        // Add hasReactionKey in zSetPostKey (sort by created date)
        redisTemplate.opsForZSet().add(zSetPostKey, hashReactionKey, timestamp);

        Map<String, String> fields = new HashMap<>();
        fields.put("id", temporaryReactionId);
        fields.put("type", type.name());
        fields.put("createdDate",  String.valueOf(timestamp));
        fields.put("userId", String.valueOf(user.getId()));
        fields.put("postId", String.valueOf(postId));
        fields.put("username", user.getUsername());

        redisTemplate.opsForHash().putAll(hashReactionKey, fields);

        ReactionDto reactionDto = new ReactionDto();
        reactionDto.setId(temporaryReactionId);
        reactionDto.setPostId(postId);
        reactionDto.setUsername(user.getUsername());
        reactionDto.setUserId(user.getId());
        reactionDto.setCreatedDate(timestamp);
        reactionDto.setType(type);

        // Send message into queue in RabbitMQ
        rabbitTemplate.convertAndSend(Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.exchange.name")),
                                      Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.save")),
                                      reactionDto);

    }

    @Transactional
    @Override
    public void removeReaction(int postId, int reactionId) {
        // 1. Tạo key cho hash
        String hashReactionKey = ReactionUtil.generateReactionHashKey(String.valueOf(reactionId));

        // 2. Lấy type của reaction từ hash để build key của ZSet
        Object typeObj = redisTemplate.opsForHash().get(hashReactionKey, "type");
        if (typeObj == null) {
            // Không tìm thấy reaction trong Redis => khỏi cần xoá
            return;
        }
        String type = typeObj.toString();

        // 3. Build ZSet key
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(postId, type);

        // 4. Xoá hash và xoá zset
        redisTemplate.opsForZSet().remove(zSetPostKey, hashReactionKey);
        redisTemplate.delete(hashReactionKey);

        rabbitTemplate.convertAndSend(Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.post.reaction.delete")),
                reactionId);
    }



    @Override
    @Transactional
    public Map<String, Integer> statsReactionByPostId(int postId) {
        String key = "post:" + postId + ":reactionStats";

        // Kiểm tra xem key có tồn tại trong Redis không
        if (!redisTemplate.hasKey(key)) {
            // Nếu không có trong cache, lấy từ DB
            Map<String, Integer> stats = this.reactionRepository.statsReactionByPostId(postId);

            // Sử dụng HashOperations với kiểu rõ ràng
            HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
            hashOps.putAll(key, stats);

            // Đặt thời gian hết hạn
            redisTemplate.expire(key, 10, TimeUnit.MINUTES);

            return stats;
        }

        // Lấy dữ liệu từ Redis với kiểu rõ ràng
        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        Map<String, Integer> result = hashOps.entries(key);

        return result;
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
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(postId, type);

        Set<String> reactionKeys = redisTemplate.opsForZSet().range(zSetPostKey, 0, -1);
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
            reactionDtos = loadReactionsFromDbAndCache(postId, type);
        }

        return reactionDtos;
    }

    private List<ReactionDto> loadReactionsFromDbAndCache(int postId, String type) {
        List<Reaction> reactions = reactionRepository.getTypeReactionsByPostId(postId, type);
        List<ReactionDto> reactionDtos = new ArrayList<>();

        for (Reaction reaction : reactions) {
            // Chuyển reaction từ DB sang ReactionDto
            ReactionDto reactionDto = ReactionMapper.toReactionDto(reaction);
            reactionDtos.add(reactionDto);

            saveReactionToRedis(reactionDto);
        }

        return reactionDtos;
    }

    // Lưu reaction vào Redis
    private void saveReactionToRedis(ReactionDto reactionDto) {
        String hashReactionKey = ReactionUtil.generateReactionHashKey(reactionDto.getId());
        String zSetPostKey = ReactionUtil.generatePostReactionZSetKey(reactionDto.getPostId(), reactionDto.getType().name());
        long timestamp = reactionDto.getCreatedDate();

        // Lưu reaction vào Hash trong Redis
        Map<String, String> fields = new HashMap<>();
        fields.put("id", reactionDto.getId());
        fields.put("type", reactionDto.getType().name());
        fields.put("createdDate", String.valueOf(timestamp));
        fields.put("userId", String.valueOf(reactionDto.getUserId()));
        fields.put("postId", String.valueOf(reactionDto.getPostId()));
        fields.put("username", reactionDto.getUsername());

        redisTemplate.opsForHash().putAll(hashReactionKey, fields);

        // Lưu reaction vào ZSet với timestamp
        redisTemplate.opsForZSet().add(zSetPostKey, hashReactionKey, timestamp);
    }
}
