package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:gestion-stock@dev-tech.com}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendStockAlertEmail(String to, String articleName, Double stock, String niveau) {
        String subject = "[ALERTE STOCK] " + articleName + " - Niveau " + niveau;
        String body = String.format(
                "<html><body>" +
                "<h2>Alerte de stock</h2>" +
                "<p>L'article <strong>%s</strong> a atteint un niveau de stock %s.</p>" +
                "<p>Stock actuel : <strong>%.2f</strong></p>" +
                "<p>Veuillez prendre les mesures nécessaires.</p>" +
                "</body></html>",
                articleName, niveau, stock
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendCommandeConfirmation(String to, String commandeCode, String clientNom) {
        String subject = "[COMMANDE] Confirmation - " + commandeCode;
        String body = String.format(
                "<html><body>" +
                "<h2>Confirmation de commande</h2>" +
                "<p>Bonjour %s,</p>" +
                "<p>Votre commande <strong>%s</strong> a été enregistrée avec succès.</p>" +
                "<p>Vous serez notifié dès que votre commande sera prête.</p>" +
                "</body></html>",
                clientNom, commandeCode
        );
        sendEmail(to, subject, body);
    }

    @Override
    public void sendFactureEmail(String to, String factureCode, List<String> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[FACTURE] " + factureCode);
            helper.setText("Veuillez trouver ci-joint la facture " + factureCode, false);

            if (attachments != null) {
                for (String filePath : attachments) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        helper.addAttachment(file.getName(), file);
                    }
                }
            }
            mailSender.send(message);
            log.info("Facture email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send facture email to {}: {}", to, e.getMessage());
        }
    }
}
