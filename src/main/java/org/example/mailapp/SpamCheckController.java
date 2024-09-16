package org.example.mailapp;

import org.example.mailapp.entities.reception.InboxMessage;
import org.example.mailapp.services.EmailClassificationService;
import org.example.mailapp.services.EmailService;
import org.example.mailapp.spam.SpamRequest;
import org.example.mailapp.spam.SpamResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class SpamCheckController {
    @Autowired
    private EmailClassificationService emailService;

    @Autowired
    private RestTemplate restTemplate; // For calling the Flask API

    // Endpoint to check if inbox messages are spam or not
    @GetMapping("/checkSpam")
    public ResponseEntity<?> checkInboxForSpam() {
        List<InboxMessage> inboxMessages = emailService.fetchEmails(); // Retrieve all inbox messages
        List<SpamResponse> results = new ArrayList<>();
        for (InboxMessage email : inboxMessages) {
            SpamRequest spamRequest = new SpamRequest(email.getBody());
            SpamResponse spamResponse = restTemplate.postForObject("http://127.0.0.1:5000/checkSpam", spamRequest, SpamResponse.class);
            results.add(spamResponse);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
