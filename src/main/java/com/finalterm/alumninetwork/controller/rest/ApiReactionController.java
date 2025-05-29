package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.response.ReactionDto;
import com.finalterm.alumninetwork.pojo.*;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.ReactionService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/posts")
public class ApiReactionController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReactionService reactionService;

    @GetMapping("/{postId}/reactions/stats")
    public ResponseEntity<Map<String, Integer>> getReactions(@PathVariable(value = "postId") int postId) {
        Map<String, Integer> stats = this.reactionService.statsReactionByPostId(postId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<ReactionDto> getReactionByPostIdAndUserId(@PathVariable(value = "postId") int postId) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (user == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(reactionService.getReactionByPostIdAndUserId(postId, user.getId()), HttpStatus.OK);
    }

    @GetMapping("/{postId}/reactions/typeReaction")
    public ResponseEntity<List<ReactionDto>> getReactionTypeByPostId(@RequestParam Map<String, String> params, @PathVariable(value = "postId") int postId) {

        String type = params.get("type");
        int page = Integer.valueOf(params.get("page"));

        return new ResponseEntity<>(reactionService.getTypeReactionByPostId(postId, type, page), HttpStatus.OK);

    }

}
