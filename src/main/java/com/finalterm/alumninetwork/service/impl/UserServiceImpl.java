package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getAllAdmin() {
        return this.userRepository.getAllAdmin();
    }
}
