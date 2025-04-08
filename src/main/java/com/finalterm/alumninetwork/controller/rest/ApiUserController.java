package com.finalterm.alumninetwork.controller.rest;

import com.cloudinary.provisioning.Account;
import com.finalterm.alumninetwork.dto.ConfirmUserDto;
import com.finalterm.alumninetwork.dto.LoginDto;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/register",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void addUser(@RequestParam Map<String, String> params,
                        @RequestPart("avatar") MultipartFile avatar) {
        this.userService.addUser(params, avatar);
    }

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity
                .ok(this.userService.login(loginDto.getUsername(), loginDto.getPassword()));
    }

//    @DeleteMapping("/delete/{username}")
//    @CrossOrigin
//    public ResponseEntity<Boolean> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("username") String username) {
//        if (userDetails == null)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        this.userService.deleteUser(username);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/confirm")
//    @CrossOrigin
//    @ResponseStatus(HttpStatus.OK)
//    public void confirmUser(@Valid @RequestBody ConfirmUserDto confirmUserDto) {
//        this.userService.confirmUSer(confirmUserDto.getUsername());
//    }
//
//    private boolean checkAdminUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return false;
//        }
//
//        Principal principal = (Principal) authentication.getPrincipal();
//        if (principal instanceof User) {
//            User user = (User) principal;
//            if (user.getRole() == UserRole.ROLE_ADMIN) {
//                return true;
//            }
//        }
//        return false;
//    }

//    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @CrossOrigin
//    @ResponseStatus(HttpStatus.CREATED)
//    public void registerUser(@RequestPart("user") RegisterDto user,
//                             @RequestPart("avatar") MultipartFile avatar,
//                             @RequestPart Map<String, String> params1) {
//        Map<String, String> params = new HashMap<>();
//        params.put("username", user.getUsername());
//        params.put("password", user.getPassword());
//        params.put("fullName", user.getFullName());
//        params.put("email", user.getEmail());
//        params.put("phone", user.getPhone());
//        params.put("role", user.getRole());
//    }
}
