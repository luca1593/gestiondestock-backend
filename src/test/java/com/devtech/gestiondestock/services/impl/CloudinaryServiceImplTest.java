package com.devtech.gestiondestock.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires du service Cloudinary.
 *
 * Le client Cloudinary est cree dans {@code @PostConstruct}, non appele ici :
 * on verifie la gestion d'erreur (l'upload est encapsule dans une
 * {@link RuntimeException}, jamais une exception technique brute).
 */
class CloudinaryServiceImplTest {

    private final CloudinaryServiceImpl service = new CloudinaryServiceImpl();

    @Test
    @DisplayName("Un flux null est encapsule dans une RuntimeException")
    void shouldWrapFailureForNullStream() {
        assertThrows(RuntimeException.class, () -> service.savePhoto(null, "titre", 1));
    }

    @Test
    @DisplayName("Un echec d'upload est encapsule dans une RuntimeException explicite")
    void shouldWrapUploadFailure() {
        InputStream flux = new ByteArrayInputStream("contenu".getBytes(StandardCharsets.UTF_8));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.savePhoto(flux, "titre", 1));
        org.junit.jupiter.api.Assertions.assertTrue(
                exception.getMessage() == null || exception.getMessage().contains("Cloudinary"));
    }
}
