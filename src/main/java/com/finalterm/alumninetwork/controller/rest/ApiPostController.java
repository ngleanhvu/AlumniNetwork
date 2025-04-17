package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/posts")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;


    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPost() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User u = userService.getUserByUsername(username);

        if (u == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(this.postService.getMyPosts(u.getId()));
    }


    //Tạo một bài Post -> có thể gửi Images hoặc không
    @PostMapping(path = "",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @CrossOrigin
    public ResponseEntity<PostDTO> uploadPostOrUpdate(@RequestParam(value = "content") String content,
                                                   @RequestParam(value = "postId", required = false) Integer postId, //For update
                                                   @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());

        Map<String, String> params = new HashMap<>();
        params.put("content", content);

        if (postId != null) {
            params.put("postId", String.valueOf(postId));
        }

        return ResponseEntity.ok(this.postService.saveOrUpdate(params, images, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        this.postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/block_comment")
    public ResponseEntity<PostDTO> blockComment(@RequestParam(value = "postId") int postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());
        Post post = this.postService.getPostById(postId);

        if (post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!post.getUser().getId().equals(user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        else {
            this.postService.lockComments(post);
            return ResponseEntity.noContent().build();
        }
    }
}
