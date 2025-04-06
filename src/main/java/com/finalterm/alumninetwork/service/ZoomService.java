package com.finalterm.alumninetwork.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ZoomService {
    String getAccessToken() throws JsonProcessingException;
    String createMeeting() throws Exception;
}
