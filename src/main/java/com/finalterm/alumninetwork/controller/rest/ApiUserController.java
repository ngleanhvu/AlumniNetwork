package com.finalterm.alumninetwork.controller.rest;

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
@Validated
public class ApiUserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "",
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
    public ResponseEntity<Boolean> loginUser(@Valid @RequestBody LoginDto loginDto) {
        if(this.userService.login(loginDto.getUsername(), loginDto.getPassword()))
            return new ResponseEntity<>(true, HttpStatus.OK);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }
}
