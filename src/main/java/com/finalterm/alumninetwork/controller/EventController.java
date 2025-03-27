package com.finalterm.alumninetwork.controller;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.service.EventService;
import com.finalterm.alumninetwork.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;

    //---------------------------------ROLE ADMIN-------------------------------------
    @GetMapping("/admin")
    public String manageEvent(Model model) {
        model.addAttribute("events", this.eventService.getEvents());
        return "events";
    }

    @GetMapping("/admin/add")
    public String createEvent(Model model) {
        model.addAttribute("event", new Event());
        return  "event-forms";
    }
    @PostMapping("/admin/add")
    public String createOrSave(@ModelAttribute("event") Event e) {
        this.eventService.saveOrUpdate(e);
        return "redirect:/events/admin";
    }


    @GetMapping("/admin/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        eventService.deleteEvent(id);
        return  "redirect:/events/admin";
    }


}
