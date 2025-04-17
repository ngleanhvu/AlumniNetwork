package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.Post;
import com.finalterm.alumninetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;


@Controller
@RequestMapping("/posts/admin")
public class PostController {

    @Autowired
    private PostService postService;

    //---------------------------------ROLE ADMIN-------------------------------------
    @GetMapping()
    public String managePost(Model model) {
        model.addAttribute("posts", this.postService.getPosts());
        return "posts"; // -> Trang thêm post
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        postService.delete(id);
        return  "redirect:/posts/admin";
    }

    @GetMapping("/statistics")
    public String loadStatistics(Model model) {
        int currentYear = Year.now().getValue();
        String timeType = "MONTH";

        List<Object[]> stats = postService.statisticPosts(timeType, currentYear);

        model.addAttribute("stats", stats);
        model.addAttribute("timeType", timeType);
        model.addAttribute("year", currentYear);
        model.addAttribute("selectedTimeType", timeType);
        model.addAttribute("selectedYear", currentYear);

        return "posts-stats";
    }

    @PostMapping("/statistics")
    public String statisticsByYearAndTimeType(@RequestParam String timeType, @RequestParam int year, Model model) {
        List<Object[]> stats = this.postService.statisticPosts(timeType, year);

        model.addAttribute("stats", stats);
        model.addAttribute("timeType", timeType);
        model.addAttribute("year", year);
        model.addAttribute("selectedTimeType", timeType);
        model.addAttribute("selectedYear", year);

        return "posts-stats";
    }

}
