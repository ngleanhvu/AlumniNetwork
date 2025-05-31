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
    public ResponseEntity<List<ReactionDto>> getTypeStatsByPostId(@PathVariable(name = "postId") int postId, @RequestBody Map<String, String> params) {
        Post post = this.postService.getPostById(postId);

        if (post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String type = params.get("type");
        if (type == null || (!type.equals("LIKE") && !type.equals("LOVE") && !type.equals("HAHA"))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int page = params.get("page") == null ? 1 : Integer.parseInt(params.get("page"));

        return new ResponseEntity<>(this.reactionService.getTypeReactionByPostId(postId, type, page), HttpStatus.OK);
    }

}
