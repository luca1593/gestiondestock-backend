package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.RoleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de role.
 *
 * Seul le nom du role est obligatoire.
 */
class RoleValidatorTest {

    @Test
    @DisplayName("Role null : une erreur")
    void shouldRejectNullRole() {
        List<String> errors = RoleValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le role", errors.get(0));
    }

    @Test
    @DisplayName("Role avec nom : aucune erreur")
    void shouldAcceptValidRole() {
        assertTrue(RoleValidator.validate(RoleDto.builder().roleNom("ADMIN").build()).isEmpty());
    }

    @Test
    @DisplayName("Nom de role vide : une erreur")
    void shouldRejectMissingRoleNom() {
        List<String> errors = RoleValidator.validate(RoleDto.builder().roleNom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le role", errors.get(0));
    }
}
