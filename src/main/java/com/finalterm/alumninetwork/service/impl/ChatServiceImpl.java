package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private Environment environment;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String chat(String userMessage) {
        String url = environment.getProperty("groq.api_url");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(environment.getProperty("groq.api_key")));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3-70b-8192");

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "Bạn là trợ lý sinh viên, giúp trả lời quy định nhà trường."),
                Map.of("role", "user", "content", userMessage)
        );
        body.put("messages", messages);
        body.put("temperature", 0.2);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map choice = ((List<Map>) response.getBody().get("choices")).get(0);
        Map message = (Map) choice.get("message");
        return (String) message.get("content");
    }
}
