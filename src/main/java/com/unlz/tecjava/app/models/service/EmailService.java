package com.unlz.tecjava.app.models.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitenteEmail;

    public void sendSimpleEmail(String receptorEmail, String asunto, String mensaje) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remitenteEmail);
        message.setTo(receptorEmail);
        message.setText(mensaje);
        message.setSubject(asunto);
        mailSender.send(message);
        ///.System.out.println("Mail Send...");
    }
}
