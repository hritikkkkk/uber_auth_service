package com.hritik.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - Uber Clone");

            String verificationUrl = frontendUrl + "/verify-email?token=" + token;

            String emailBody = String.format(
                    "Welcome to Uber Clone!\n\n" +
                            "Please click the link below to verify your email address:\n" +
                            "%s\n\n" +
                            "This link will expire in 24 hours.\n\n" +
                            "If you didn't create an account, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "Uber Clone Team",
                    verificationUrl
            );

            message.setText(emailBody);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Uber Clone!");

            String emailBody = String.format(
                    "Hi %s,\n\n" +
                            "Welcome to Uber Clone! Your email has been successfully verified.\n\n" +
                            "You can now start using our services:\n" +
                            "- Book rides\n" +
                            "- Track your trips\n" +
                            "- Manage your account\n\n" +
                            "Thank you for joining us!\n\n" +
                            "Best regards,\n" +
                            "Uber Clone Team",
                    name
            );

            message.setText(emailBody);
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            // Don't throw exception here as it's not critical
        }
    }
}