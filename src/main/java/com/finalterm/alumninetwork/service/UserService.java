package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.ChangePasswordDto;
import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<User> getAllAdmin();
    void addUser(Map<String, String> params, MultipartFile avatar);
    String login(String username, String password);
    List<User> getUsers(Map<String, String> params);
    boolean deleteUser(String username);
    boolean confirmUser(String username);
    boolean resetTimePassword(LecturerInfo lecturerInfo);
    User getUserByUsername(String username);
    boolean changePassword(ChangePasswordDto changePasswordDto);
}
