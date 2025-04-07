package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.dto.LoginDto;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            String token = userService.login(loginDto.getUsername(), loginDto.getPassword());
            return ResponseEntity.ok().body(Map.of("Token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("Error", e.getMessage()));
        }
    }


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
