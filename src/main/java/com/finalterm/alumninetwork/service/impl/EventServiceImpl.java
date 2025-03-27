package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.repository.EventRepository;
import com.finalterm.alumninetwork.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;
    @Override
    public List<Event> getEvents() {
        return this.eventRepository.getAll();
    }
    @Override
    @Transactional
    public Event saveOrUpdate(Event event) {
        return this.eventRepository.saveOrUpdate(event);
    }
    @Override
    @Transactional
    public void deleteEvent(int id) {
        this.eventRepository.delete(id);
    }
}
