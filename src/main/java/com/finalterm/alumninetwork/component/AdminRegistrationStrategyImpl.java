package com.finalterm.alumninetwork.component;

import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.pojo.UserRole;
import com.finalterm.alumninetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("admin")
public class AdminRegistrationStrategyImpl implements UserRegistrationStrategy {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void register(User user, Map<String, String> params) {
        UserRole userRole = UserRole.ROLE_ADMIN;
        user.setRole(userRole);
        user.setActive(true);
        userRepository.saveUser(user);
    }
}
