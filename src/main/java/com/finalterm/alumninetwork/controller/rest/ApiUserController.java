package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.dto.ChangePasswordDto;
import com.finalterm.alumninetwork.dto.LoginDto;
import com.finalterm.alumninetwork.dto.ResponseUserDto;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private Environment env;

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


    @GetMapping("/{username}")
    @CrossOrigin
    public ResponseEntity<ResponseUserDto> getUserByUsername(@PathVariable String username) {
        User user = this.userService.getUserByUsername(username);
        ResponseUserDto responseUserDto = new ResponseUserDto();

        responseUserDto.setId(user.getId());
        responseUserDto.setFullName(user.getFullName());
        responseUserDto.setUsername(user.getUsername());
        responseUserDto.setEmail(user.getEmail());
        responseUserDto.setPhone(user.getPhone());
        responseUserDto.setAvatar(user.getAvatar());
        responseUserDto.setCoverAvatar(user.getCoverAvatar());
        responseUserDto.setRole(user.getRole().name());

        return ResponseEntity.ok(responseUserDto);
    }

    @GetMapping("/current-user")
    @CrossOrigin
    public ResponseEntity<?> getCurrentUser() {
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setId(user.getId());
        responseUserDto.setFullName(user.getFullName());
        responseUserDto.setUsername(user.getUsername());
        responseUserDto.setEmail(user.getEmail());
        responseUserDto.setPhone(user.getPhone());
        responseUserDto.setAvatar(user.getAvatar());
        responseUserDto.setCoverAvatar(user.getCoverAvatar());
        responseUserDto.setRole(user.getRole().name());
        return ResponseEntity.ok(responseUserDto);
    }

    @CrossOrigin
    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idTokenStr = payload.get("idToken");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(env.getProperty("google.client_id")))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(idTokenStr);
            if (idToken != null) {
                String email = idToken.getPayload().getEmail();
                User user = this.userService.getUserByEmail(email);
                String jwt = this.jwtService.generateTokenLogin(user.getUsername());
                return ResponseEntity.ok(Map.of("token", jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID Token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying token");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@Valid @RequestParam Map<String, String> payload) {
        return new ResponseEntity<>(this.userService.getUsers(payload), HttpStatus.OK);
    }
}

