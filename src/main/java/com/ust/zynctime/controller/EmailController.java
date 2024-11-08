package com.ust.zynctime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ust.zynctime.dto.EmailRequest;
import com.ust.zynctime.security.service.ApiUserService; // Assuming this service will get user roles

@RestController
@RequestMapping("/api/timezone/email")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApiUserService apiUserService;  // This service will get the user details, including their roles

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        // Fetch the roles of the current user
        if (!apiUserService.hasRole(currentUser, "ADMIN") && !apiUserService.hasRole(currentUser, "MANAGER")) {
            return ResponseEntity.status(403).body("Access denied: You do not have permission to schedule meetings.");
        }

        // Print the request details for debugging
        System.out.println(request.toString());

        // Split the email addresses provided in the request
        String[] emailAddresses = request.getEmails().split(",");

        // Loop through each email and send the meeting invitation
        try {
            for (String email : emailAddresses) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("rajirajeshwari552@gmail.com");  // Use your sender email
                message.setTo(email.trim());  // Trim spaces around emails and set the recipient
                message.setSubject(request.getTitle());  // Set the subject as the meeting title
                message.setText(createMeetingDetails(request));  // Create and set the body with meeting details

                // Send the email
                mailSender.send(message);
            }
            return ResponseEntity.ok("Emails sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send the email. Please try again later.");
        }
    }

    // Method to format meeting details into a string
    private String createMeetingDetails(EmailRequest request) {
        return "Meeting Title: " + request.getTitle() + "\n" +
                "Date: " + request.getDate() + "\n" +
                "Time: " + request.getTime() + "\n" +
                "Video Conferencing Platform: " + request.getMode() + "\n" +
                "Participants: " + request.getEmails() + "\n\n" +
                "Please join the meeting on the specified platform.";
    }
}
