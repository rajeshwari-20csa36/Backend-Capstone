package com.ust.zynctime.controller;

import com.ust.zynctime.dto.EmailResponse;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ust.zynctime.dto.EmailRequest;


import lombok.extern.slf4j.Slf4j;



@Slf4j
@RestController
@RequestMapping("/api/employee/email")
@Validated
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")  // Restrict to admin and organizer roles
    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            String[] emailAddresses = request.getEmails().split(",");
            for (String email : emailAddresses) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("rajirajeshwar552@gmail.com");
                message.setTo(email.trim());
                message.setSubject(request.getTitle());
                message.setText(String.format("""
                Date: %s
                Time: %s
                Mode: %s""",
                        request.getDate(),
                        request.getTime(),
                        request.getMode()));
                mailSender.send(message);

            }
            return ResponseEntity.ok(EmailResponse.success("Emails sent successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(EmailResponse.error("Failed to send emails", e.getMessage()));
        }
    }

    private String buildEmailContent(EmailRequest request) {
        return String.format("""
            Date: %s
            Time: %s
            Mode: %s
            """,
                request.getDate(),
                request.getTime(),
                request.getMode()
        );
    }
}