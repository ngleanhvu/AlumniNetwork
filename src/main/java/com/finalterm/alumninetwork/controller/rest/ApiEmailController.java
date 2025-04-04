package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.dto.EmailRecord;
import com.finalterm.alumninetwork.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class ApiEmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestParam String to,
                          @RequestParam String subject,
                          @RequestParam String body) {
        this.emailService.sendEmail(to, subject, body);
    }
}
