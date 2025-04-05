package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.EmailRecord;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void receiveEmail();
}
