//package com.finalterm.alumninetwork.controller.rest;
//
//import com.finalterm.alumninetwork.dto.PostDTO;
//import com.finalterm.alumninetwork.pojo.Comment;
//import com.finalterm.alumninetwork.pojo.Post;
//import com.finalterm.alumninetwork.pojo.User;
//import com.finalterm.alumninetwork.service.CommentService;
//import com.finalterm.alumninetwork.service.PostService;
//import com.finalterm.alumninetwork.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/api/comments")
//public class ApiCommentController {
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private CommentService commentService;
//
////    @GetMapping
////    public ResponseEntity<List<Comment>> getAllParentComments() {
////
////        return ResponseEntity.ok(this.commentService.getRootCommentsByPostId());
////    }
//
//    //Tạo một bài Post -> có thể gửi Images hoặc không
//    @PostMapping(path = "",
//            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE}
//    )
//    @CrossOrigin
//    public ResponseEntity<Post> uploadPost(@RequestParam("content") String content ,@RequestParam(value = "images", required = false) List<MultipartFile> images) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = this.userService.getUserByUsername(auth.getName());
//
//        return ResponseEntity.ok(this.postService.saveOrUpdate(content, images, user));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable int id) {
//        this.postService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}
