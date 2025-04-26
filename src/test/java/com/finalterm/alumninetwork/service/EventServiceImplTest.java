//package com.finalterm.alumninetwork.service;
//
//import com.finalterm.alumninetwork.dto.EmailRecord;
//import com.finalterm.alumninetwork.pojo.Event;
//import com.finalterm.alumninetwork.pojo.GroupNetwork;
//import com.finalterm.alumninetwork.pojo.User;
//import com.finalterm.alumninetwork.repository.EventRepository;
//import com.finalterm.alumninetwork.service.impl.EventServiceImpl;
//import org.hibernate.validator.internal.engine.groups.Group;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.core.env.Environment;
//
//import java.util.*;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class EventServiceImplTest {
//
//    @InjectMocks
//    private EventServiceImpl eventService;
//
//    @Mock
//    private EventRepository eventRepository;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private GroupService groupService;
//
//    @Mock
//    private RabbitTemplate rabbitTemplate;
//
//    @Mock
//    private Environment env;
//
//    List<User> users = new ArrayList<>();
//    List<GroupNetwork> groupNetworks = new ArrayList<>();
//
//    @BeforeEach
//    void setUp() {
//        User u1 = new User();
//        u1.setId(1);
//        u1.setUsername("member1");
//        u1.setPassword("vu1@gmail.com");
//
//        User u2 = new User();
//        u2.setId(2);
//        u2.setUsername("member2");
//        u2.setPassword("vu2@gmail.com");
//
//        User u3 = new User();
//        u3.setId(3);
//        u3.setUsername("member3");
//        u3.setPassword("vu3@gmail.com");
//
//        users.addAll(Arrays.asList(u1, u2, u3));
//
//    }
//
//    @Test
//    @Tag("sendEmail")
//    void testSendEmail_Success() {
//        Integer[] userIdsArray = new Integer[]{1, 2, 3};
//        List<Integer> userIds = List.of(userIdsArray);
//        List<Integer> groupNetworkIds = List.of(1);
//
//        User u4 = new User();
//        u4.setId(4);
//        u4.setUsername("member4");
//        u4.setEmail("vu4@gmail.com");
//        List<User> groupUsers = List.of(u4);
//
//        Event event = new Event();
//        event.setId(1);
//        event.setTitle("title");
//        event.setContent("content");
//        event.setDescription("description");
//        event.setStartTime(new Date());
//        event.setEndTime(new Date());
//
//        when(env.getProperty("rabbitmq.exchange.name")).thenReturn("emailExchange");
//        when(env.getProperty("rabbitmq.routing.key.name")).thenReturn("emailRoutingKey");
//        when(userService.getUserByIds(userIds)).thenReturn(users);
//        when(groupService.getUserGroupNetworksByIds(groupNetworkIds)).thenReturn(groupUsers);
//
//        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(EmailRecord.class));
//
//        eventService.sendEvent(event, userIds, groupNetworkIds);
//
//        verify(rabbitTemplate, times(4)).convertAndSend(eq("emailExchange"), eq("emailRoutingKey"), any(EmailRecord.class));
//    }
//}
