package com.devtech.gestiondestock.services;

import java.io.InputStream;

public interface GoogleDriveService {
    // Méthode pour téléverser un fichier et retourner son URL
    String savePhoto(InputStream photo, String context, Integer id) throws Exception;
}
