package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.Event;

import java.util.List;

public interface EventService {
    List<Event> getEvents();
    Event saveOrUpdate(Event event);
    void deleteEvent(int id);
}
