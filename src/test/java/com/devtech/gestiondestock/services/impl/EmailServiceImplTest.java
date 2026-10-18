package com.devtech.gestiondestock.services.impl;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du service d'envoi d'email.
 *
 * Le {@link JavaMailSender} est mocke : on verifie l'orchestration (creation du
 * message, champs, pieces jointes) et la tolerance aux erreurs SMTP.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender);
        ReflectionTestUtils.setField(emailService, "fromEmail", "no-reply@test.com");
    }

    private MimeMessage newMimeMessage() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    @Test
    @DisplayName("sendEmail construit et envoie un message avec les bons champs")
    void sendEmailShouldBuildAndSend() throws Exception {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendEmail("client@test.com", "Sujet", "<p>Corps</p>");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertTrue(sent.getSubject().contains("Sujet"));
        assertTrue(sent.getAllRecipients()[0].toString().contains("client@test.com"));
    }

    @Test
    @DisplayName("Une erreur SMTP est avalee (pas d'exception propagee a l'appelant)")
    void sendEmailShouldSwallowMessagingException() {
        // Une adresse d'expediteur vide fait lever une AddressException a
        // MimeMessageHelper.setFrom(...) : elle doit etre capturee et journalisee
        // sans remonter a l'appelant (comportement actuel : echec silencieux).
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());
        ReflectionTestUtils.setField(emailService, "fromEmail", "");

        assertDoesNotThrow(() -> emailService.sendEmail("client@test.com", "Sujet", "Corps"));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendStockAlertEmail construit le sujet d'alerte puis delegue a l'envoi")
    void sendStockAlertShouldDelegate() {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendStockAlertEmail("resp@test.com", "Vis", 3.5d, "CRITIQUE");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendCommandeConfirmation delegue a l'envoi")
    void sendCommandeConfirmationShouldDelegate() {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendCommandeConfirmation("client@test.com", "CMD-001", "Jean");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendFactureEmail ignore les pieces jointes dont le fichier n'existe pas")
    void sendFactureShouldSkipMissingAttachments() {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendFactureEmail("client@test.com", "FAC-001",
                List.of("/chemin/inexistant/facture.pdf"));

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendFactureEmail envoie aussi sans piece jointe (liste vide)")
    void sendFactureShouldSendWithoutAttachments() {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendFactureEmail("client@test.com", "FAC-002", List.of());

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Un corps vide n'empeche pas l'envoi")
    void sendEmailShouldHandleEmptyBody() {
        when(mailSender.createMimeMessage()).thenReturn(newMimeMessage());

        emailService.sendEmail("client@test.com", "Sujet", "");

        verify(mailSender).send(any(MimeMessage.class));
    }
}
