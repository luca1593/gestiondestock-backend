package com.devtech.gestiondestock.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link SavePhotoParams} (aucune conversion entite).
 */
class SavePhotoParamsTest {

    private SavePhotoParams sample() {
        return SavePhotoParams.builder()
                .title("Photo article")
                .id(42)
                .context("article")
                .build();
    }

    @Test
    @DisplayName("le builder renseigne tous les champs")
    void builderShouldFillAllFields() {
        SavePhotoParams params = sample();

        assertEquals("Photo article", params.getTitle());
        assertEquals(Integer.valueOf(42), params.getId());
        assertEquals("article", params.getContext());
    }

    @Test
    @DisplayName("les setters modifient les champs")
    void settersShouldUpdateFields() {
        SavePhotoParams params = SavePhotoParams.builder().build();
        params.setTitle("Photo client");
        params.setId(7);
        params.setContext("client");

        assertEquals("Photo client", params.getTitle());
        assertEquals(Integer.valueOf(7), params.getId());
        assertEquals("client", params.getContext());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        SavePhotoParams a = sample();
        SavePhotoParams b = sample();
        SavePhotoParams c = SavePhotoParams.builder().id(1).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("un DTO vide (builder sans champ) laisse les champs a null")
    void emptyBuilderShouldLeaveFieldsNull() {
        SavePhotoParams params = SavePhotoParams.builder().build();

        assertNull(params.getTitle());
        assertNull(params.getId());
        assertNull(params.getContext());
    }
}
