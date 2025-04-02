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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<User> getAllAdmin() {
        return this.userRepository.getAllAdmin();
    }

    @Override
    public void addUser(Map<String, String> params, MultipartFile file) throws RuntimeException {
        // Add common information
        User user = new User();
        user.setUsername(params.get("username"));
        user.setPassword(bCryptPasswordEncoder.encode(params.get("password")));
        user.setEmail(params.get("email"));
        user.setPhone(params.get("phone"));
        user.setActive(false);
        user.setFullName(params.get("fullName"));
        user.setCreatedAt(new Date());

        // Upload file
        if (file.isEmpty()) throw new IllegalArgumentException("file is empty");
        Map res = null;
        try {
            res = this.cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.setAvatar(res.get("secure_url").toString());

        // Set role and add other information with each user role
        String role = params.get("role");
        saveUserWithRole(role, user, params);

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
                LecturerInfo lecturerInfo = new LecturerInfo();
                lecturerInfo.setUser(user);
                lecturerInfo.setChangedPassword(false);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date()); // Lấy ngày hiện tại
                calendar.add(Calendar.HOUR, 24); // Cộng thêm 24 giờ
                lecturerInfo.setExpiredResetPasswordTime(calendar.getTime());
                lecturerInfoRepository.addLecturerInfo(lecturerInfo);
                break;
            default:
                userRole = UserRole.ROLE_ALUMNI;
                String studentCode = params.getOrDefault("studentCode","");
                if (studentCode.isEmpty()) throw new IllegalArgumentException("student code is empty");
                user.setRole(userRole);
                userRepository.addUser(user);
                AlumniInfo alumniInfo = new AlumniInfo();
                alumniInfo.setStudentCode(studentCode);
                alumniInfo.setUser(user);
                alumniInfoRepository.addAlumniInfo(alumniInfo);
        }
    }
}
