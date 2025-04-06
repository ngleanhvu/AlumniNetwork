package com.finalterm.alumninetwork.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalterm.alumninetwork.service.ZoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@PropertySource("classpath:config.properties")
public class ZoomServiceImpl implements ZoomService {
    @Autowired
    private Environment env;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAccessToken() throws JsonProcessingException {
        String tokenUrl = "https://zoom.us/oauth/token" +
                "?grant_type=account_credentials&account_id=" + env.getProperty("zoom.account_id");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(Objects.requireNonNull(env.getProperty("zoom.client_id")),
                Objects.requireNonNull(env.getProperty("zoom.client_secret")));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    public String createMeeting() throws Exception {
        String accessToken = getAccessToken();

        String apiUrl = "https://api.zoom.us/v2/users/me/meetings";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> meeting = new HashMap<>();
        meeting.put("topic", "Cuộc họp từ Spring MVC");
        meeting.put("type", 1); // 1 = instant meeting
        meeting.put("settings", Map.of("host_video", true, "participant_video", true));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(meeting, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        return jsonNode.get("join_url").asText();
    }
}
