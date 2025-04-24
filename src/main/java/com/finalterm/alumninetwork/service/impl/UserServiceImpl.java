package com.finalterm.alumninetwork.service.impl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.dto.ChangePasswordDto;
import com.finalterm.alumninetwork.dto.ResponseUserDto;
import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.AlumniInfoRepository;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.EmailService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String USER_PREFIX = "user";

    @Autowired
    private JwtService jwtService;

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
        if (file.isEmpty())
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
        userRegistrationService.registerUser(role, user, params);

    }

    @Override
    public User findUserById(int id) {
        return this.userRepository.getUserById(id);
    }

    @Override
    public String login(String username, String password) {
        User user = this.userRepository.getUserByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        if (!user.getActive())
            throw new RuntimeException("User not active");
        if(!bCryptPasswordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Incorrect password");
        return jwtService.generateTokenLogin(username);
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        return this.userRepository.getUsers(params);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        User user = this.userRepository.getUserById(userId);
        this.userRepository.deleteUser(user);
        return true;
    }

    @Override
    public boolean confirmUser(Integer userId) {
        User user = this.userRepository.getUserById(userId);
        if (user == null)
            throw new RuntimeException("User not found");
        user.setActive(true);
        this.userRepository.saveUser(user);
        return true;
    }

    @Override
    public boolean resetTimePassword(LecturerInfo lecturerInfo) {
        LecturerInfo existingLecturerInfo = this.lecturerInfoRepository.getLecturerInfoById(lecturerInfo.getId());
        if (existingLecturerInfo == null)
            throw new RuntimeException("LecturerInfo not found");
        existingLecturerInfo.setExpiredResetPasswordTime(lecturerInfo.getExpiredResetPasswordTime());
        User user = existingLecturerInfo.getUser();
        user.setActive(true);
        userRepository.saveUser(user);
        lecturerInfoRepository.saveLecturerInfo(existingLecturerInfo);
        return true;
    }

    @Override
    public User getUserByUsername(String username) {
        User user;
        String key = String.format("%s:%s:%s", USER_PREFIX, "username", username);
        user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            return user;
        }
        user = this.userRepository.getUserByUsername(username);
        redisTemplate.opsForValue().set(key, user, 5, TimeUnit.MINUTES);
        return user;
    }


    @Override
    public boolean changePassword(ChangePasswordDto changePasswordDto) {
        if (!changePasswordDto.getPassword().equals(changePasswordDto.getConfirmPassword()))
            throw new RuntimeException("Password do not match");
        User user = this.userRepository.getUserByEmail(changePasswordDto.getEmail());
        if (user == null)
            throw new RuntimeException("User not found");
        if (user.getRole() != UserRole.ROLE_LECTURER)
            throw new RuntimeException("User not lecturer");
        LecturerInfo lecturerInfo = user.getLecturerInfo();
        if (lecturerInfo.getExpiredResetPasswordTime().before(new Date()))
            throw new RuntimeException("Expired reset password");
        lecturerInfo.setChangedPassword(true);
        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getPassword()));
        user.setActive(true);
        this.userRepository.saveUser(user);
        this.lecturerInfoRepository.saveLecturerInfo(lecturerInfo);
        return true;
    }

    @Override
    public List<User> getAllUserExactAdmin() {
        return this.userRepository.getAllUserExactAdmin();
    }

    @Override
    public List<User> getUserByIds(List<Integer> userIds) {
        return this.userRepository.getUserByIds(userIds);
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.getUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.userRepository.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole().name()));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

}
