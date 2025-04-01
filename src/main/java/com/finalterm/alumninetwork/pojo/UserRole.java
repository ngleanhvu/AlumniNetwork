package com.finalterm.alumninetwork.pojo;

import com.finalterm.alumninetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public enum UserRole {
    ROLE_ALUMNI,
    ROLE_ADMIN,
    ROLE_LECTURER;
}
