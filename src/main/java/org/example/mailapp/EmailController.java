package org.example.mailapp;

import org.example.mailapp.entities.reception.InboxMessage;
import org.example.mailapp.entities.*;
import org.example.mailapp.repositories.reception.*;
import org.example.mailapp.repositories.*;
import org.example.mailapp.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class EmailController {
    @Autowired private EmailService emailService;
    @Autowired private EmailClassificationService emailClassificationService;
    @Autowired private StorageService storageService;

    @Autowired EmailRepository emailRepository;
    @Autowired InboxRepository inboxRepository;
    @Autowired ReclamationRepository reclamationRepository;
    @Autowired FactureRepository factureRepository;
    @Autowired DemandeDeCongeRepository demandeDeCongeRepository;

    @Autowired SentAttachmentRepository sentAttachmentRepository;
    @Autowired ReceivedAttachmentRepository receivedAttachmentRepository;

    public EmailController(EmailRepository emailRepository, InboxRepository inboxRepository,
           ReceivedAttachmentRepository receivedAttachmentRepository, SentAttachmentRepository sentAttachmentRepository) {
        this.emailRepository = emailRepository;
        this.inboxRepository = inboxRepository;
        this.receivedAttachmentRepository = receivedAttachmentRepository;
        this.sentAttachmentRepository = sentAttachmentRepository;
    }


// sending service
    @GetMapping("/emails")
    public List<Email> getEmails(){
        return (List<Email>) emailRepository.findAll();
    }

    @GetMapping("emails/{id}")
    public Email getEmailsById(@PathVariable Long id) {
        return emailRepository.findById(id).orElse(null);
    }

    @PostMapping("/emails")
    public String sendEmail(@RequestBody Email email) {
        try {
            System.out.println(email.toString());
            if(email.getPathToAttachment() == null) {
                emailService.sendSimpleMessage(email);
            }else {
                emailService.sendMessageWithAttachment(email);
            }
            emailRepository.save(email);
            // emailService.sendValidationEmail(email.getToEmail());
            return "Email saved and sent successfully to " + email.getToEmail();
        } catch (MailException e) {
            return "Error sending email";
        }
    }

    @DeleteMapping("emails/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        try {
            emailService.deleteEmailById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

// receiving service
    @GetMapping("/inbox")
    public List<InboxMessage> getInbox() {
        return (List<InboxMessage>) inboxRepository.findAll();
                // emailClassificationService.fetchEmails();
    }

    @GetMapping("/inbox/type/{type}")
    public List<InboxMessage> getInboxByType(@PathVariable String type){
        return inboxRepository.findByType(type);
    }

    @GetMapping("inbox/{id}")
    public InboxMessage getInboxById(@PathVariable Long id) {
        return inboxRepository.findById(id.toString()).orElse(null);
    }

    @GetMapping("/sentAttachments")
    public List<SentAttachment> getSentAttachments(){
        return (List<SentAttachment>) sentAttachmentRepository.findAll();
    }

    @GetMapping("/sentAttachments/{id}")
    public SentAttachment getSentAttachmentById(@PathVariable Long id){
        return sentAttachmentRepository.findById(id.toString()).orElse(null);
    }

    @GetMapping("/receivedAttachments")
    public List<ReceivedAttachment> getReceivedAttachments(){
        return (List<ReceivedAttachment>) receivedAttachmentRepository.findAll();
    }

    @GetMapping("/receivedAttachments/{filename}")
    public ReceivedAttachment getReceivedAttachmentByFilename(@PathVariable String filename){
        byte[] content = storageService.downloadReceivedFile(filename);
        return new ReceivedAttachment(filename, content);
    }

    /*@PostMapping("/uploadReceivedAttachment")
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
        String upload = storageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(upload);
    }*/

    // ai model
    @PostMapping("/generate")
    public String getOpenaiResponse(@RequestBody String prompt) {
        /*try {
            ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", prompt);
            ChatResponse response = restTemplate.postForObject(
                    "https://api.openai.com/v1/chat/completions", chatRequest, ChatResponse.class);
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {*/
            return "Sorry, something went wrong while generating the email content.";
        //}
    }
}