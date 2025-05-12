package com.finalterm.alumninetwork.mapper;

import com.finalterm.alumninetwork.dto.response.UserWithPostDto;
import com.finalterm.alumninetwork.pojo.User;

public class UserWithPostMapper {

        public static UserWithPostDto toUserWithPostDto(User user) {
            UserWithPostDto userWithPostDto = new UserWithPostDto();
            userWithPostDto.setAvatar(user.getAvatar());
            userWithPostDto.setFullName(user.getFullName());
            userWithPostDto.setUserId(user.getId());
            userWithPostDto.setUsername(user.getUsername());

        return userWithPostDto;
    }
}
