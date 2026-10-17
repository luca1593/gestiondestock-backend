package com.devtech.gestiondestock.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link ChangerMotDePasseUtilisateurDto} (aucune conversion entite).
 */
class ChangerMotDePasseUtilisateurDtoTest {

    private ChangerMotDePasseUtilisateurDto sample() {
        return ChangerMotDePasseUtilisateurDto.builder()
                .id(12)
                .motDePasse("nouveauSecret")
                .confirmMotDePasse("nouveauSecret")
                .build();
    }

    @Test
    @DisplayName("le builder renseigne tous les champs")
    void builderShouldFillAllFields() {
        ChangerMotDePasseUtilisateurDto dto = sample();

        assertEquals(Integer.valueOf(12), dto.getId());
        assertEquals("nouveauSecret", dto.getMotDePasse());
        assertEquals("nouveauSecret", dto.getConfirmMotDePasse());
    }

    @Test
    @DisplayName("les setters modifient les champs")
    void settersShouldUpdateFields() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder().build();
        dto.setId(5);
        dto.setMotDePasse("abc");
        dto.setConfirmMotDePasse("abc");

        assertEquals(Integer.valueOf(5), dto.getId());
        assertEquals("abc", dto.getMotDePasse());
        assertEquals("abc", dto.getConfirmMotDePasse());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        ChangerMotDePasseUtilisateurDto a = sample();
        ChangerMotDePasseUtilisateurDto b = sample();
        ChangerMotDePasseUtilisateurDto c = ChangerMotDePasseUtilisateurDto.builder().id(99).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("un DTO vide (builder sans champ) laisse les champs a null")
    void emptyBuilderShouldLeaveFieldsNull() {
        ChangerMotDePasseUtilisateurDto dto = ChangerMotDePasseUtilisateurDto.builder().build();

        assertNull(dto.getId());
        assertNull(dto.getMotDePasse());
        assertNull(dto.getConfirmMotDePasse());
    }
}
