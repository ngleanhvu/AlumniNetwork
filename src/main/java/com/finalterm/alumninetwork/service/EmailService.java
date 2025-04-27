package com.finalterm.alumninetwork.service;

import com.finalterm.alumninetwork.dto.EmailRecord;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void receiveEmails(List<EmailRecord> emailRecords);
}
