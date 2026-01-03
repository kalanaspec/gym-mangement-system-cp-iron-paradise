package com.gym.management.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendExpirationReminder(String to, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Subscription Expiration Reminder");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public void sendMembershipExpirationReminder(String to, String memberName, LocalDateTime expirationDate) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Membership Renewal Reminder - Gym Management System");
        
        String formattedDate = expirationDate.format(DATE_FORMATTER);
        String emailBody = String.format(
            "Dear %s,\n\n" +
            "This is a friendly reminder that your gym membership will expire on %s.\n\n" +
            "To continue enjoying our facilities and services, please renew your membership before the expiration date.\n\n" +
            "If you have any questions or need assistance with the renewal process, please don't hesitate to contact us.\n\n" +
            "Thank you for being a valued member!\n\n" +
            "Best regards,\n" +
            "Gym Management Team",
            memberName != null ? memberName : "Valued Member",
            formattedDate
        );
        
        mailMessage.setText(emailBody);
        mailSender.send(mailMessage);
    }
}

