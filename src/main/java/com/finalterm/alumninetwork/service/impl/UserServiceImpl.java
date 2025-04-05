package com.finalterm.alumninetwork.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.pojo.AlumniInfo;
import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.AlumniInfoRepository;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private AlumniInfoRepository alumniInfoRepository;
    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;
    @Autowired
    private Environment environment;

    @Override
    public List<User> getAllAdmin() {
        return this.userRepository.getAllAdmin();
    }

    @Override
    public void addUser(Map<String, String> params, MultipartFile file) throws RuntimeException {
        // Add common information
        User user = new User();
        user.setUsername(params.get("username"));
        user.setPassword(bCryptPasswordEncoder.encode(params.getOrDefault("password",
                environment.getProperty("lecturer.info.password"))));
        user.setEmail(params.get("email"));
        user.setPhone(params.get("phone"));
        user.setActive(false);
        user.setFullName(params.get("fullName"));
        user.setCreatedAt(new Date());

        // Upload file
        if (file == null || file.isEmpty())
            user.setAvatar("https://res.cloudinary.com/dea1l3vvu/image/upload/v1743673326/avatar_ieqlcg.jpg");
        else {
            Map res = null;
            try {
                res = this.cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            user.setAvatar(res.get("secure_url").toString());
        }

        // Set role and add other information with each user role
        String role = params.get("role");
        saveUserWithRole(role, user, params);

    }


    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.getUserByUsername(username);
    }

    @Override
    public boolean login(String username, String password) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        if (!user.getActive())
            throw new RuntimeException("User not active");

        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    private void saveUserWithRole(String role, User user, Map<String, String> params) {
        UserRole userRole;
        switch (role) {
            case "admin":
                userRole = UserRole.ROLE_ADMIN;
                user.setRole(userRole);
                userRepository.addUser(user);
                break;

            case "lecturer":
                userRole = UserRole.ROLE_LECTURER;
                user.setRole(userRole);
                user.setActive(true);
                userRepository.addUser(user);
                // them thoi gian thay doi mat khau
                LecturerInfo lecturerInfo = new LecturerInfo();
                lecturerInfo.setUser(user);
                lecturerInfo.setChangedPassword(false);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR,
                        Integer.parseInt(Objects.requireNonNull(environment.getProperty("lecturer.info.time.reset.password"))));// Lấy ngày hiện tại
                lecturerInfo.setExpiredResetPasswordTime(calendar.getTime());
                lecturerInfoRepository.addLecturerInfo(lecturerInfo);
//                // gui mail
//                emailService.sendEmail(user.getEmail(), "Account Info", user.getUsername());
                break;
            default:
                userRole = UserRole.ROLE_ALUMNI;
                user.setRole(userRole);
                userRepository.addUser(user);
                // them mssv
                String studentCode = params.getOrDefault("studentCode","");
                if (studentCode.isEmpty()) throw new IllegalArgumentException("student code is empty");
                AlumniInfo alumniInfo = new AlumniInfo();
                alumniInfo.setStudentCode(studentCode);
                alumniInfo.setUser(user);
                alumniInfoRepository.addAlumniInfo(alumniInfo);
        }
    }

    @Override
    public User findUserById(int id) {
        return this.userRepository.getUserById(id);
    }
}
