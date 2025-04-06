package com.finalterm.alumninetwork.repository;

import com.finalterm.alumninetwork.pojo.Event;

import java.util.List;

public interface EventRepository {
    List<Event> getAll();
    Event saveOrUpdate(Event e);
    void delete(int id);
}
