package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.response.EventDTO;
import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.EventService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class ApiEventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getEvents(@RequestParam(name = "page", defaultValue = "1", required = false) Integer page) {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        User user = this.userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Event> events = eventService.getEvents(params);
        List<EventDTO> eventDTOs = events.stream()
                .map(event -> {
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setId(event.getId());
                    eventDTO.setTitle(event.getTitle());
                    eventDTO.setStartTime(event.getStartTime());
                    eventDTO.setEndTime(event.getEndTime());
                    return eventDTO;
                })
                .toList();
        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }
}
