package com.finalterm.alumninetwork.component;

import com.finalterm.alumninetwork.pojo.User;

import java.util.Map;

public interface UserRegistrationStrategy {
    void register(User user, Map<String, String> params);
}
