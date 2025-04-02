package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllAdmin();
    void addUser(User user);
    User getUserByUsername(String username);
}
