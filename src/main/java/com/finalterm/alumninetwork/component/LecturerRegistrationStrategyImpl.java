package com.finalterm.alumninetwork.component;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component("lecturer")
public class LecturerRegistrationStrategyImpl implements UserRegistrationStrategy {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Environment environment;

    @Override
    public void register(User user, Map<String, String> params) {
        user.setRole(UserRole.ROLE_LECTURER);
        user.setActive(true);
        userRepository.saveUser(user);

        LecturerInfo lecturerInfo = new LecturerInfo();
        lecturerInfo.setUser(user);
        lecturerInfo.setChangedPassword(false);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR,
                Integer.parseInt(Objects.requireNonNull(environment.getProperty("lecturer.info.time.reset.password"))));
        lecturerInfo.setExpiredResetPasswordTime(calendar.getTime());

        lecturerInfoRepository.saveLecturerInfo(lecturerInfo);
        emailService.sendEmail(user.getEmail(), "Account Info", user.getUsername());
    }
}
