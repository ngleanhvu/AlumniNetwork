package com.finalterm.alumninetwork.repository;
import com.finalterm.alumninetwork.pojo.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllAdmin();
    void saveUser(User user);
    Optional<User> getUserByUsername(String username);
    void saveAllUser(List<User> users);
    List<User> getUsers(Map<String, String> params);
    void deleteUser(User user);
    User getUserByEmail(String email);
    User getUserById(Integer userId);
    List<User> getAllUserExactAdmin();
    List<User> getUserByIds(List<Integer> userIds);
    int countUsers();
}
