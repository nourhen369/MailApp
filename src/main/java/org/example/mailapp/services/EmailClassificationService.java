package org.example.mailapp.services;

import jakarta.mail.*;
import jakarta.mail.search.*;
import org.example.mailapp.entities.ReceivedAttachment;
import org.example.mailapp.entities.reception.*;
import org.example.mailapp.repositories.ReceivedAttachmentRepository;
import org.example.mailapp.repositories.reception.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class EmailClassificationService {

    @Autowired private ReceivedAttachmentRepository receivedAttachmentRepository;
    @Autowired private InboxRepository inboxRepository;

    @Autowired private ReclamationRepository reclamationRepository;
    @Autowired private FactureRepository factureRepository;
    @Autowired private DemandeDeCongeRepository demandeDeCongeRepository;

    private List<String> reclamationKeywords = Arrays.asList(
            "réclamation", "plainte", "problème", "défaillance", "incident", "mécontentement", "insatisfaction",
            "contestation", "grief", "remarque", "retour", "erreur", "souci", "réclamer", "insatisfait"
    );
    private List<String> factureKeywords = Arrays.asList(
            "facture", "paiement", "invoice", "reçu", "note de frais", "preuve d'achat", "solde",
            "montant dû", "règlement", "dépense", "facturation", "honoraires", "quittance",
            "avis de paiement", "bordereau"
    );
    private List<String> demandeCongeKeywords = Arrays.asList(
            "congé", "vacances", "demande de congé", "absence", "jour de repos", "congés payés",
            "repos", "permission", "pause", "demande de vacances", "jours de congé", "congés annuels",
            "demande de permission", "retrait temporaire", "congé maladie"
    );

    public void classifyAndSaveEmail(InboxMessage email) {
        String subject = email.getSubject().toLowerCase();
        if (matchesKeywords(subject, reclamationKeywords)) {
            Reclamation reclamation = new Reclamation();
            email.setType("RECLAMATION");
            copyEmailProperties(email, reclamation);
            reclamationRepository.save(reclamation);
        } else if (matchesKeywords(subject, factureKeywords)) {
            Facture facture = new Facture();
            email.setType("FACTURE");
            copyEmailProperties(email, facture);
            factureRepository.save(facture);
        } else if (matchesKeywords(subject, demandeCongeKeywords)) {
            DemandeDeConge demandeDeConge = new DemandeDeConge();
            email.setType("DEMANDEDECONGE");
            copyEmailProperties(email, demandeDeConge);
            demandeDeCongeRepository.save(demandeDeConge);
        } else {
            email.setType("InboxMessage");
            inboxRepository.save(email);
        }
    }

    private void copyEmailProperties(InboxMessage source, InboxMessage target) {
        target.setFromEmail(source.getFromEmail());
        target.setSubject(source.getSubject());
        target.setBody(source.getBody());
        target.setFilename(source.getFilename());
        target.setType(source.getType());
    }

    private boolean matchesKeywords(String subject, List<String> keywords) {
        return keywords.stream().anyMatch(subject::contains);
    }

    private static Store openStore() {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "nourhenekhechine@gmail.com", "dnfpbedrfgymzjyo");
            return store;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<InboxMessage> fetchEmails() {
        try {
            Store store = openStore();
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);

            SearchTerm searchTerm = new AndTerm(
                    new FlagTerm(new Flags(Flags.Flag.RECENT), false),
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false)
            );
            // Message messages[] = inbox.search(searchTerm);
            Message messages[] = inbox.getMessages();

            if (messages.length>0) {
                inboxRepository.deleteAll();
                receivedAttachmentRepository.deleteAll();
                resetAutoIncrement();
            } else return (List<InboxMessage>) inboxRepository.findAll(); // no new messages

            for (Message message: messages) {
                InboxMessage inboxMessage = new InboxMessage();
                inboxMessage.setSubject(message.getSubject());
                String fromEmail = message.getFrom()[0].toString();
                inboxMessage.setFromEmail(fromEmail);

                String plainText = getText(message);
                inboxMessage.setBody(plainText);

                List<ReceivedAttachment> attachments = extractAttachments(message, fromEmail);
                receivedAttachmentRepository.saveAll(attachments);
                this.classifyAndSaveEmail(inboxMessage);
            }
            inbox.close();
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (List<InboxMessage>) inboxRepository.findAll();
    }

    private String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            if (p.isMimeType("text/html")) {
                return Jsoup.parse(s).text();
            }
            return s;
        } else if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }
    private List<ReceivedAttachment> extractAttachments(Part p, String fromEmail) throws MessagingException, IOException {
        List<ReceivedAttachment> attachments = new ArrayList<>();
        if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                String disposition = bp.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    attachments.add(readReceivedAttachment(bp, fromEmail));
                } else if (bp.isMimeType("multipart/*")) {
                    attachments.addAll(extractAttachments(bp, fromEmail));
                }
            }
        }
        return attachments;
    }
    private ReceivedAttachment readReceivedAttachment(BodyPart bp, String fromEmail) throws MessagingException, IOException {
        String fileName = bp.getFileName();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = bp.getInputStream();
        byte[] buf = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buf)) != -1) {
            baos.write(buf, 0, bytesRead);
        }
        byte[] content = baos.toByteArray();
        return new ReceivedAttachment(fromEmail, fileName, content);
    }
    private void resetAutoIncrement() {
        String sql = "ALTER TABLE inbox_message AUTO_INCREMENT = 1";
        inboxRepository.executeUpdate(sql);
    }

}
