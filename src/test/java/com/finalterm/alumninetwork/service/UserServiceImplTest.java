package com.finalterm.alumninetwork.service;

import com.cloudinary.Cloudinary;
import com.finalterm.alumninetwork.component.JwtService;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtService jwtService;

    List<User> users = new ArrayList<>();

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("member1");

        User u2 = new User();
        u2.setId(2);
        u2.setUsername("member2");

        User u3 = new User();
        u3.setId(3);
        u3.setUsername("member3");

        users.addAll(Arrays.asList(u1, u2, u3));
    }

    @Test
    @Tag("getUser")
    void testGetUserByUsername() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("anhvu");
        mockUser.setEmail("anhvu@gmail.com");

        when(userRepository.getUserByUsername("anhvu")).thenReturn(mockUser);

        User user = userService.getUserByUsername("anhvu");

        assertEquals("anhvu@gmail.com", user.getEmail());
    }

    @Test
    @Tag("getUser")
    void testGetUsers_NoParameters() {
        when(userRepository.getUsers(null)).thenReturn(users);
        assertEquals(users, userService.getUsers(null));
    }

    @Test
    @Tag("getUser")
    void testGetUsers_WithParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "member");
        users.remove(0);
        when(userRepository.getUsers(params)).thenReturn(users);
        assertEquals(users, userService.getUsers(params));
    }


    @Test
    @Tag("login")
    void testLogin_UserNotFound_ShouldThrowException() {
        String username = "unknown";
        String password = "12345678";

        when(userRepository.getUserByUsername(username)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(username, password));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @Tag("login")
    void testLogin_UserNotActive_ShouldThrowException() {
        String username = "anhvu";
        String password = "12345678";

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(username);
        mockUser.setEmail("anhvu@gmail.com");
        mockUser.setActive(false);

        when(userRepository.getUserByUsername(username)).thenReturn(mockUser);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(username, password));

        assertEquals("User not active", exception.getMessage());
    }

    @Test
    @Tag("login")
    void testLogin_IncorrectPassword_ShouldThrowException() {
        String username = "anhvu";
        String password = "12345678";

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(username);
        mockUser.setEmail("anhvu@gmail.com");
        mockUser.setActive(true);
        mockUser.setPassword("1234567");

        when(userRepository.getUserByUsername(username)).thenReturn(mockUser);
        when(bCryptPasswordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.login(username, password));

        assertEquals("Incorrect password", exception.getMessage());
    }

    @Test
    @Tag("login")
    void testLogin_Success() {
        String username = "anhvu";
        String password = "rawPassword";
        String jwtToken = "123j.12o3j12io.12oj31i";

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername(username);
        mockUser.setPassword("hashedPassword");
        mockUser.setActive(true);

        when(userRepository.getUserByUsername(username)).thenReturn(mockUser);
        when(bCryptPasswordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateTokenLogin(username)).thenReturn(jwtToken);

        String token = userService.login(username, password);
        // dùng để kiểm thử hành vi, xem thử hành vi đó đã thực hiện bao nhiêu lần
        verify(userRepository, times(1)).getUserByUsername(username);

        assertEquals(jwtToken, token);
    }


}
