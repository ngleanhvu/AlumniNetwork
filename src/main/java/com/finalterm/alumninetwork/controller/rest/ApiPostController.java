package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.response.FeedResponseDto;
import com.finalterm.alumninetwork.dto.response.PostDTO;
import com.finalterm.alumninetwork.pojo.EnumReaction;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Path;
import java.util.Date;
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
    private Environment env;

    @GetMapping("/profile") //Go to profile ->
    public ResponseEntity<List<PostDTO>> getAllPost(@RequestParam(required = false) Long createdAt,
                                                    @RequestParam(required = false) Integer pageSize) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User u = userService.getUserByUsername(username);

            int limit = (pageSize != null) ? pageSize : env.getProperty("pagination.post_size", Integer.class, 5);
            Date cursorDate = (createdAt != null) ? new Date(createdAt) : new Date();

            return ResponseEntity.ok(this.postService.getMyPosts(u.getId(), cursorDate, limit));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body((List<PostDTO>) Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}") // -> Go to user's profile
    public ResponseEntity<List<PostDTO>> getPostsFromProfile(@RequestParam(required = false) Long createdAt,
                                                             @RequestParam(required = false) Integer pageSize,
                                                             @PathVariable (value = "userId") int userId) {
        int limit = (pageSize != null) ? pageSize : env.getProperty("pagination.page_size", Integer.class, 5);
        Date cursorDate = (createdAt != null) ? new Date(createdAt) : new Date();

        return ResponseEntity.ok(this.postService.getMyPosts(userId, cursorDate, limit));
    }

    //Tạo một bài Post -> có thể gửi Images hoặc không
    @PostMapping(path = "",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @CrossOrigin
    public ResponseEntity<?> uploadPostOrUpdate(@RequestParam(value = "content") String content,
                                                      @RequestParam(value = "postId", required = false) Integer postId, //For update
                                                      @RequestParam(value = "title") String title,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = this.userService.getUserByUsername(auth.getName());

            Map<String, String> params = new HashMap<>();
            params.put("content", content);
            params.put("title", title);

            if (postId != null) {
                params.put("postId", String.valueOf(postId));
            }

            return ResponseEntity.ok(this.postService.saveOrUpdate(params, images, user));
        } catch (Exception e) {
            e.printStackTrace(); // ➜ In lỗi đầy đủ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable int id) {
        this.postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/blockComment")
    public ResponseEntity<PostDTO> blockComment(@PathVariable(value = "postId") int postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());
        Post post = this.postService.getPostById(postId);

        if (post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!post.getUser().getId().equals(user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        else {
            this.postService.lockOrUnlockComments(post);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/feeds")
    public ResponseEntity<FeedResponseDto> getFeeds(@RequestParam(required = false) Long createdAt,
                                                    @RequestParam(required = false) Integer limitFeed) {
        int limit = (limitFeed != null) ? limitFeed : env.getProperty("pagination.post_size", Integer.class, 5);
        Date cursorDate = (createdAt != null) ? new Date(createdAt) : new Date();

        return ResponseEntity.ok(this.postService.loadGlobalFeed(cursorDate, limit));
    }

//    @GetMapping("/{postId}")
//    public ResponseEntity<?> getPostById(@PathVariable int postId) {
//        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
//        if (user == null)
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        Post post = this.postService.getPostById(postId);
//        return ResponseEntity.ok(post);
//    }

    @PostMapping("/{postId}/reactions/toggle")
    public ResponseEntity<?> toggleReaction(@PathVariable(value = "postId") int postId, @RequestBody Map<String, String> params) {
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (user == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        String type = params.get("type") == null ? "" : params.get("type");

        Post post = this.postService.getPostById(postId);

        this.postService.toggleReaction(post, user, EnumReaction.valueOf(type));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
