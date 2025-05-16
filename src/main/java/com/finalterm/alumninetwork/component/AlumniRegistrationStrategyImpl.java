package com.finalterm.alumninetwork.component;

import com.finalterm.alumninetwork.pojo.AlumniInfo;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.AlumniInfoRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("alumni")
public class AlumniRegistrationStrategyImpl implements UserRegistrationStrategy {

    @Autowired private UserRepository userRepository;
    @Autowired private AlumniInfoRepository alumniInfoRepository;

    @Override
    public void register(User user, Map<String, String> params) {
        user.setRole(UserRole.ROLE_ALUMNI);
        userRepository.saveUser(user);

        String studentCode = params.getOrDefault("studentCode", "");
        if (studentCode.isEmpty()) throw new IllegalArgumentException("student code is empty");

        AlumniInfo alumniInfo = new AlumniInfo();
        alumniInfo.setStudentCode(studentCode);
        alumniInfo.setUser(user);
        alumniInfoRepository.addAlumniInfo(alumniInfo);
    }
}
