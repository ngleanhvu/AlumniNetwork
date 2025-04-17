package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.EventService;
import com.finalterm.alumninetwork.service.GroupService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    @GetMapping("/admin")
    public String manageEvent(Model model,
                              @ModelAttribute("kw") String kw,
                              @ModelAttribute("startTime") String startTime,
                              @ModelAttribute("endTime") String endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("kw", kw);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        model.addAttribute("events", this.eventService.getEvents(params));
        return "events";
    }

    @GetMapping("/admin/add")
    public String createEvent(Model model) {
        model.addAttribute("event", new Event());
        return  "event-forms";
    }

    @PostMapping("/admin/add")
    public String createOrSave(@ModelAttribute("event") Event e,
                               @ModelAttribute("offline") Boolean offline) throws Exception {
        this.eventService.saveEvent(e, offline);
        return "redirect:/events/admin";
    }


    @GetMapping("/admin/delete/{id}")
    public String deletePost(@PathVariable("id") int id) {
        eventService.deleteEvent(id);
        return  "redirect:/events/admin";
    }

    @GetMapping("/admin/send-event/{id}")
    public String sendEventPage(@PathVariable("id") int id,
                            Model model) {
        model.addAttribute("event", this.eventService.getEventById(id));
        model.addAttribute("groups", this.groupService.getGroupNetworks(null));
        model.addAttribute("users", this.userService.getAllUserExactAdmin());
        return "event-send";
    }

    @PostMapping("/admin/send-email")
    public String sendEvent(@ModelAttribute("event") Event e,
                            @ModelAttribute("userIds") List<Integer> userIds,
                            @ModelAttribute("groupIds") List<Integer> groupIds) {
        Event event = this.eventService.getEventById(e.getId());
        this.eventService.sendEvent(event, userIds, groupIds);
        return "redirect:/events/admin";
    }
}
