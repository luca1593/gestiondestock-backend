package com.devtech.gestiondestock.utils;

import com.devtech.gestiondestock.model.auth.ExtendedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de l'utilitaire JWT.
 *
 * Verifie la generation, l'extraction des claims et la validation d'un token.
 * Le secret est injecte manuellement (pas de contexte Spring).
 */
class JwtUtilTest {

    private static final String SECRET =
            "testSecretKeyForUnitTestsOnlyDoNotUseInProductionEnvironment1234567890";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretString", SECRET);
    }

    private ExtendedUser user(String username, Integer idEntreprise) {
        return new ExtendedUser(username, "password", idEntreprise, Collections.emptyList());
    }

    @Test
    @DisplayName("Le token genere contient le login et l'identifiant d'entreprise")
    void shouldGenerateTokenWithClaims() {
        ExtendedUser user = user("admin@test.com", 5);

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertEquals("admin@test.com", jwtUtil.extractUsername(token));
        assertEquals("5", jwtUtil.extractIdEntreprise(token));
        assertNotNull(jwtUtil.extractExpiration(token));
        assertTrue(jwtUtil.extractExpiration(token).after(new java.util.Date()));
    }

    @Test
    @DisplayName("validateToken accepte le bon utilisateur et refuse un autre")
    void shouldValidateTokenAgainstUser() {
        ExtendedUser user = user("admin@test.com", 5);
        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.validateToken(token, user), "Le meme utilisateur doit etre valide");
        assertFalse(jwtUtil.validateToken(token, user("autre@test.com", 5)),
                "Un autre login ne doit pas etre valide");
    }

    @Test
    @DisplayName("Un token falsifie ne doit pas etre exploitable")
    void shouldRejectTamperedToken() {
        String token = jwtUtil.generateToken(user("admin@test.com", 5));
        String tampered = token.substring(0, token.length() - 2) + "xy";

        assertThrows(RuntimeException.class, () -> jwtUtil.extractUsername(tampered));
    }
}
