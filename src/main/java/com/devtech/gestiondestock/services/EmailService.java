package com.devtech.gestiondestock.services;

import java.util.List;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendStockAlertEmail(String to, String articleName, Double stock, String niveau);
    void sendCommandeConfirmation(String to, String commandeCode, String clientNom);
    void sendFactureEmail(String to, String factureCode, List<String> attachments);
}
