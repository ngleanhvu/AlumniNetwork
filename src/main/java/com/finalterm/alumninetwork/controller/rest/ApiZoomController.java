package com.finalterm.alumninetwork.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalterm.alumninetwork.service.ZoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zoom")
public class ApiZoomController {
    @Autowired
    private ZoomService zoomService;

    @PostMapping("/create")
    @CrossOrigin
    public ResponseEntity<String> getMeeting() throws JsonProcessingException {
        try {
            return ResponseEntity
                    .ok()
                    .body(zoomService.createMeeting());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

}
