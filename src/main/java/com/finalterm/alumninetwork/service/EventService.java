package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.GroupNetwork;
import com.finalterm.alumninetwork.pojo.User;

import java.util.List;
import java.util.Map;

public interface EventService {
    List<Event> getEvents(Map<String, String> params);
    void saveEvent(Event event, Boolean offline) throws Exception;
    void deleteEvent(Integer id);
    void sendEvent(Event event, List<Integer> userIds, List<Integer> groupNetworkIds);
    Event getEventById(Integer eventId);
    long countEvents();
}
