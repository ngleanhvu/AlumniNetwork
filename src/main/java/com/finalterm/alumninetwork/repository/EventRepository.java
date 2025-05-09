package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Event;

import java.util.List;
import java.util.Map;

public interface EventRepository {
    List<Event> getAllEvent(Map<String, String> params);
    Event saveEvent(Event e);
    boolean deleteEventById(Integer eventId);
    Event getEventById(Integer eventId);
    long countEvents();
}
