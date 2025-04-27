package com.finalterm.alumninetwork.service.impl;

import com.finalterm.alumninetwork.dto.EmailRecord;
import com.finalterm.alumninetwork.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void sendEmail(String to, String subject, String body) {
        EmailRecord emailRecord = new EmailRecord(to, subject, body);
        rabbitTemplate.convertAndSend(Objects.requireNonNull(env.getProperty("rabbitmq.exchange.name")),
                Objects.requireNonNull(env.getProperty("rabbitmq.routing.key.name")),
                emailRecord);
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    @Override
    public void receiveEmails(List<EmailRecord> emailRecords) {
        SimpleMailMessage message = new SimpleMailMessage();
        emailRecords.forEach(emailRecord -> {
            message.setSubject(emailRecord.subject());
            message.setText(emailRecord.body());
            message.setTo(emailRecord.to());
            mailSender.send(message);
        });
    }
}
