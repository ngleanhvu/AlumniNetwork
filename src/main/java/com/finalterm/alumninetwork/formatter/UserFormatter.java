package com.finalterm.alumninetwork.formatter;

import com.finalterm.alumninetwork.pojo.User;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class UserFormatter implements Formatter<User> {
    @Override
    public User parse(String userId, Locale locale) throws ParseException {
        User u = new User();
        u.setId(Integer.valueOf(userId));
        return u;
    }
    @Override
    public String print(User user, Locale locale) {
        return String.valueOf(user.getId());
    }
}
