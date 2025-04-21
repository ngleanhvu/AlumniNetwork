package com.finalterm.alumninetwork.controller.rest;

import com.finalterm.alumninetwork.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ApiChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<String> ask(@RequestBody Map<String, String> req) {
        String userInput = req.get("question");
        String reply = chatService.chat(userInput);
        return ResponseEntity.ok(reply);
    }
}
