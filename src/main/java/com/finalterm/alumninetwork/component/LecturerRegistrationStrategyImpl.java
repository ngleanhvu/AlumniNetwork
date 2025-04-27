package com.finalterm.alumninetwork.component;

import com.finalterm.alumninetwork.dto.EmailRecord;
import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.EmailService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("lecturer")
public class LecturerRegistrationStrategyImpl implements UserRegistrationStrategy {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

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
                Integer.parseInt(Objects.requireNonNull(env.getProperty("lecturer.info.time.reset.password"))));
        lecturerInfo.setExpiredResetPasswordTime(calendar.getTime());

        lecturerInfoRepository.saveLecturerInfo(lecturerInfo);

        List<EmailRecord> emailRecord = new ArrayList<>(1);
        rabbitTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routing.key.name")),
                emailRecord
        );
    }
}
