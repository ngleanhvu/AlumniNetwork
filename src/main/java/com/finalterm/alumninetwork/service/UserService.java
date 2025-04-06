package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<User> getAllAdmin();
    void addUser(Map<String, String> params, MultipartFile avatar);
    boolean login(String username, String password);
}
