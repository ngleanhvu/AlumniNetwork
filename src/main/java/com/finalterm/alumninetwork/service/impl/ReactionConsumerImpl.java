package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.Reaction;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.ReactionRepository;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionConsumer;
import com.finalterm.alumninetwork.service.ReactionService;
import com.finalterm.alumninetwork.service.UserService;
import com.finalterm.alumninetwork.util.ReactionUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ReactionConsumerImpl implements ReactionConsumer {

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private Environment env;

    @RabbitListener(queues = "post.reaction.db.queue", containerFactory = "rabbitListenerContainerFactory")
    @Override
    public void saveReaction(ReactionDto reactionDto) {
        // Get User
        User user = this.userService.findUserById(reactionDto.getUserId());

        // Get Post
        Post post = this.postService.getPostById(reactionDto.getPostId());

        Reaction reaction = new Reaction();
        reaction.setCreatedDate(new Date(reactionDto.getCreatedDate()));
        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setType(reactionDto.getType());

        // Save reaction into database
        reactionRepository.addOrUpdateReaction(reaction);

        // Update Redis
        String newHashReactionKey = ReactionUtil.generateReactionHashKey(reaction.getId().toString());

        // Thêm hash mới
        Map<String, String> fields = new HashMap<>();
        fields.put("id", reaction.getId().toString());
        fields.put("type", reaction.getType().name());
        fields.put("createdDate", String.valueOf(reaction.getCreatedDate().getTime()));
        fields.put("userId", String.valueOf(user.getId()));
        fields.put("postId", String.valueOf(post.getId()));
        fields.put("username", user.getUsername());
        redisTemplate.opsForHash().putAll(newHashReactionKey, fields);

        redisTemplate.expire(newHashReactionKey, 2, TimeUnit.MINUTES);
//
//        // Xóa element cũ trong ZSet
//        redisTemplate.opsForZSet().remove(zSetPostKey, oldHashReactionKey);
//
//        // Add element mới vào ZSet
//        redisTemplate.opsForZSet().add(zSetPostKey, newHashReactionKey, reaction.getCreatedDate().getTime());
    }


    @RabbitListener(queues = "post.reaction.delete.queue", containerFactory = "rabbitListenerContainerFactory")
    @Override
    public void deleteReaction(int reactionId) {
        reactionRepository.deleteReactionById(reactionId);
    }
}
