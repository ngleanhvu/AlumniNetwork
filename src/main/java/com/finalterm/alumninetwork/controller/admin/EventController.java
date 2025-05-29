package com.finalterm.alumninetwork.controller.admin;

import com.finalterm.alumninetwork.dto.EmailRecord;
import com.finalterm.alumninetwork.dto.response.EventDTO;
import com.finalterm.alumninetwork.pojo.Event;
import com.finalterm.alumninetwork.pojo.User;
import com.finalterm.alumninetwork.service.EventService;
import com.finalterm.alumninetwork.service.GroupService;
import com.finalterm.alumninetwork.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private Environment env;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/admin")
    public String manageEvent(Model model,
                              @ModelAttribute("kw") String kw,
                              @ModelAttribute("startTime") String startTime,
                              @ModelAttribute("endTime") String endTime,
                              @RequestParam(name = "page", defaultValue = "1", required = false) int page) {
        Map<String, String> params = new HashMap<>();
        params.put("kw", kw);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        model.addAttribute("events", this.eventService.getEvents(params));
        long totalEvents = this.eventService.countEvents();
        int totalPages = (int) Math.ceil((double) totalEvents / (double) Integer.parseInt(
                Objects.requireNonNull(env.getProperty("PAGE_SIZE"))));
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", totalPages != 0 ? totalPages : 1);
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
                            @RequestParam(value = "userIds", required = false) List<Integer> userIds,
                            @RequestParam(value = "groupIds", required = false) List<Integer> groupIds,
                            @RequestParam(value = "all", required = false) Boolean all) {
        Event event = this.eventService.getEventById(e.getId());
        if (all != null) {
            List<User> users = this.userService.getAllUsers();

            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            eventDTO.setTitle(event.getTitle());
            eventDTO.setStartTime(event.getStartTime());
            eventDTO.setEndTime(event.getEndTime());

            List<EmailRecord> emailRecords = users.stream()
                    .map(user -> new EmailRecord(user.getEmail(), event.getTitle(), event.getContent()))
                    .toList();

            rabbitTemplate.convertAndSend(
                    Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                    Objects.requireNonNull(env.getProperty("rabbitmq.routing.key.name")),
                    emailRecords
            );
        }
        else {
            this.eventService.sendEvent(event, userIds, groupIds);
        }
        return "redirect:/events/admin";
    }

    @GetMapping("/admin/update/{id}")
    public String updateEventPage(Model model, @PathVariable("id") Integer id) {
        model.addAttribute("event", this.eventService.getEventById(id));
        return "event-forms";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteEvent(@PathVariable("id") int id) {
        this.eventService.deleteEvent(id);
        return "redirect:/events/admin";
    }
}
