package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.component.UserRegistrationStrategy;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserRegistrationService {
    private final Map<String, UserRegistrationStrategy> strategyMap;

    @Autowired
    public UserRegistrationService(List<UserRegistrationStrategy> strategies) {
        this.strategyMap = new HashMap<>();
        for (UserRegistrationStrategy strategy : strategies) {
            String role = strategy.getClass().getAnnotation(Component.class).value();
            strategyMap.put(role, strategy);
        }
    }

    public void registerUser(String role, User user, Map<String, String> params) {
        UserRegistrationStrategy strategy = strategyMap.getOrDefault(role, strategyMap.get("alumni")); // fallback to alumni
        strategy.register(user, params);
    }
}
