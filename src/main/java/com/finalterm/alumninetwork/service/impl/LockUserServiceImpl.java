package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.LecturerInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.LecturerInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.LockUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LockUserServiceImpl implements LockUserService {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;
    @Autowired
    private LecturerInfoRepository lecturerInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedRate = 3600000)
    @Override
    public void lockUsers() {
        Map<String, String> params = new HashMap<>();
        params.put("changedPassword", "false");
        List<LecturerInfo> lecturerInfos = lecturerInfoRepository.getLecturerInfos(params);
        List<User> users = new ArrayList<>();
        for (LecturerInfo lecturerInfo : lecturerInfos) {
            if (lecturerInfo.getExpiredResetPasswordTime().before(new Date())) {
                User user =lecturerInfo.getUser();
                user.setActive(false);
                users.add(user);
            }
        }
        if (!users.isEmpty()) {
            userRepository.saveAllUser(users);
        }
    }
}
