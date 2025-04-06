package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@ControllerAdvice
public class IndexController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }
}
