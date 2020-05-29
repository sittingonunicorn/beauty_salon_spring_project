package net.ukr.lina_chen.beauty_salon_spring_project.controller.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PropertySource("classpath:mail.properties")
public class MailSender {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${subject}")
    private String subject;

    @Value("${text}")
    private String text;


    private final JavaMailSender mailSender;

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String addressed, Long masterId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(addressed);
        message.setSubject(subject);
        message.setText(text + masterId);
        mailSender.send(message);
    }
}
