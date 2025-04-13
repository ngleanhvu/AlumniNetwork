package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.response.CommentDto;
import com.finalterm.alumninetwork.pojo.Comment;
import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.CommentService;
import com.finalterm.alumninetwork.service.PostService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/posts")
public class ApiCommentController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    // "--------------------------API Comments---------------------------"
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getRootComments(@PathVariable(value = "postId") int postId) {
        return new ResponseEntity<>(this.commentService.getRootCommentsByPostId(postId), HttpStatus.OK );
    }

    @GetMapping("{postId}/comments/{commentId}")
    public ResponseEntity<List<CommentDto>> getMoreReplies(@PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId) {
        Post p = this.postService.getPostById(postId);
        Comment parentComment = this.commentService.getCommentById(commentId);
        if (p == null || parentComment == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(this.commentService.getCommentsByParentCommentId(commentId), HttpStatus.OK);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@RequestBody Map<String, String> params, @PathVariable(value = "postId") int postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());
        Post p = this.postService.getPostById(postId);

        if (user == null || p == null)
            return new ResponseEntity<>(HttpStatusCode.valueOf( 404));
        else return new ResponseEntity<>(this.commentService.addComment(params, p, user), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());
        Post p = this.postService.getPostById(postId);
        Comment comment = this.commentService.getCommentById(commentId);

        if (user.getId().equals(comment.getUser().getId()) || user.getId().equals(p.getUser().getId())) {
            this.commentService.deleteComment(commentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping(path = "/{postId}/comments/{commentId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<CommentDto> updateComment(@RequestBody Map<String, String> params, @PathVariable(value = "postId") int postId, @PathVariable(value = "commentId") int commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(auth.getName());
        Comment comment = this.commentService.getCommentById(commentId);

        if (comment == null || comment.getPost().getId() != postId)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!user.equals(comment.getUser()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        System.out.println("Get new conntent");
        String newContent = params.get("content");
        if (newContent != null && !newContent.trim().isEmpty()) {
            comment.setContent(newContent);
            System.out.printf("Update content: %s", newContent);
            return new ResponseEntity<>(this.commentService.updateComment(comment), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
