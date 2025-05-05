package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.LecturerInfoService;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private LecturerInfoService lecturerInfoService;

    @GetMapping("/users/admin")
    public String manageUser(Model model,
                             @ModelAttribute("kw") String keyword) {
        Map<String, String> params = new HashMap<>();
        params.put("kw", keyword);
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("users", this.userService.getUsers(params));
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

    @PostMapping("/users/admin/delete")
    public String deleteUser(@ModelAttribute("user") User user,
                             RedirectAttributes redirectAttrs) {
        boolean check = this.userService.deleteUser(user.getId());
        if (check) {
            redirectAttrs.addFlashAttribute("msg", "Xóa thành công");
        } else {
            redirectAttrs.addFlashAttribute("msg", "Xóa thất bại");
        }
        return "redirect:/users/admin";
    }

    @PostMapping("/users/admin/confirm")
    public String confirmUser(@ModelAttribute("user") User user,
                             RedirectAttributes redirectAttrs) {
        boolean check = this.userService.confirmUser(user.getId());
        if (check) {
            redirectAttrs.addFlashAttribute("msg", "Xác nhận thành công");
        } else {
            redirectAttrs.addFlashAttribute("msg", "Xác nhận thất bại");
        }
        return "redirect:/users/admin";
    }

    @GetMapping("/users/admin/reset-time-password/{username}")
    public String resetTimePasswordPage(@PathVariable("username") String username,
                                    Model model) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        model.addAttribute("lecturer", this.lecturerInfoService.getLecturerInfos(params).get(0));
        return "lecturer-form";
    }

    @PostMapping("/users/admin/reset-time-password")
    public String resetTimePassword(@ModelAttribute("lecturer") LecturerInfo lecturerInfo) {
        if (this.userService.resetTimePassword(lecturerInfo)) {
            return "redirect:/users/admin";
        } else {
            return "lecturer-form";
        }
    }

    @GetMapping("/users/admin/update/{username}")
    public String updateUserPage(@PathVariable("username") String username, Model model) {
        model.addAttribute("user", this.userService.getUserByUsername(username));
        return "users-form";
    }
}
