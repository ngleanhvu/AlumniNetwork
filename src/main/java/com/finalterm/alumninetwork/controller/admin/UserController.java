package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/admin")
    public String manageUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        System.out.println(user);
        return "users";
    }

    @GetMapping("/users/admin/add")
    public String addUserPage(Model model) {
        model.addAttribute("user", new User());
        return "users-form";
    }

    @PostMapping("/users/admin/add")
    public String addUser(@Valid @ModelAttribute("user") User user,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users-form";
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("phone", user.getPhone());
        params.put("role", "lecturer");
        params.put("fullName", user.getFullName());
        this.userService.addUser(params, user.getFile());
        return "redirect:/users/admin";
    }

    @GetMapping("/users/admin/login")
    public String loginPage() {
        return "users-login";
    }
}
