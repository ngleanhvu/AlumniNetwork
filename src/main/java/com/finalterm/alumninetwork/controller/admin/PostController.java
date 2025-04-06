package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    //---------------------------------ROLE ADMIN-------------------------------------
    @GetMapping("/admin")
    public String managePost(Model model) {
        System.out.println(new Post());
        model.addAttribute("posts", this.postService.getPosts());
        return "posts"; // -> Trang thêm post
    }
    @GetMapping("/admin/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        postService.delete(id);
        return  "redirect:/posts/admin";
    }

    //---------------------------------ROLE CLIENT-------------------------------------
    @PostMapping("/add")
    public Post uploadPost(@RequestBody Post p) {
        return null;
    }

    @GetMapping
    public List<Post> getAllPost() {
        return this.postService.getPosts();
    }

}
