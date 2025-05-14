package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.EmailRecord;
import com.finalterm.alumninetwork.dto.response.EventDTO;
import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.repository.EventRepository;
import com.finalterm.alumninetwork.repository.UserRepository;
import com.finalterm.alumninetwork.service.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ZoomService zoomService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    @Override
    public List<Event> getEvents(Map<String, String> params) {
        return this.eventRepository.getAllEvent(params);
    }
    @Override
    @Transactional
    public void saveEvent(Event event, Boolean offline) throws Exception {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.getUserByUsername(principal.getName());
        event.setUser(user);
        if (!offline) {
            String urlZoom = this.zoomService.createMeeting();
            event.setUrlZoom(urlZoom);
        }
        this.eventRepository.saveEvent(event);
    }

    @Override
    public void deleteEvent(Integer id) {
        this.eventRepository.deleteEventById(id);
    }

    @Override
    public void sendEvent(Event event, List<Integer> userIds, List<Integer> groupNetworkIds) {
        List<User> users = new ArrayList<>();
        if (userIds != null)
            users = this.userService.getUserByIds(userIds);

        List<User> groupUsers = new ArrayList<>();
        if (groupNetworkIds != null)
            groupUsers = this.groupService.getUserGroupNetworksByIds(groupNetworkIds);

        Set<User> allReceiptUsers = new HashSet<>();

        if (!users.isEmpty()) {
            allReceiptUsers.addAll(users);
        }

        if (!groupUsers.isEmpty()) {
            allReceiptUsers.addAll(groupUsers);
        }

        if (allReceiptUsers.isEmpty()) {
            return;
        }

        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setTitle(event.getTitle());
        eventDTO.setStartTime(event.getStartTime());
        eventDTO.setEndTime(event.getEndTime());

        List<EmailRecord> emailRecords = allReceiptUsers.stream()
                .map(user -> new EmailRecord(user.getEmail(), event.getTitle(), event.getContent()))
                .toList();

        rabbitTemplate.convertAndSend(
                Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routing.key.name")),
                emailRecords
        );

    }

    @Override
    public Event getEventById(Integer eventId) {
        return this.eventRepository.getEventById(eventId);
    }

    @Override
    public long countEvents() {
        return this.eventRepository.countEvents();
    }
}
