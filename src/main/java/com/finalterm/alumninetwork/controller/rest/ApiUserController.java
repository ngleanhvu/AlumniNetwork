package com.finalterm.alumninetwork.controller.rest;

import com.cloudinary.provisioning.Account;
import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.dto.ChangePasswordDto;
import com.finalterm.alumninetwork.dto.ConfirmUserDto;
import com.finalterm.alumninetwork.dto.LoginDto;
import com.finalterm.alumninetwork.dto.ResponseUserDto;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

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

    @PostMapping("/change-password")
    @CrossOrigin
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        this.userService.changePassword(changePasswordDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current-user")
    @CrossOrigin
    public ResponseEntity<?> getCurrentUser() {
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setUsername(user.getUsername());
        responseUserDto.setEmail(user.getEmail());
        responseUserDto.setPhone(user.getPhone());
        responseUserDto.setAvatar(user.getAvatar());
        responseUserDto.setCoverAvatar(user.getCoverAvatar());
        responseUserDto.setRole(user.getRole().name());
        return ResponseEntity.ok(responseUserDto);
    }
}
