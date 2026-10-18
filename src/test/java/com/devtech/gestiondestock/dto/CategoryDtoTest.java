package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link CategoryDto}.
 */
class CategoryDtoTest {

    private CategoryDto sampleDto() {
        return CategoryDto.builder()
                .id(4)
                .code("CAT-04")
                .designation("Boissons")
                .identreprise(9)
                .build();
    }

    private Category sampleEntity() {
        Category category = new Category();
        category.setId(4);
        category.setCode("CAT-04");
        category.setDesignation("Boissons");
        category.setIdentreprise(9);
        return category;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(CategoryDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapAllFields() {
            CategoryDto dto = CategoryDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(4), dto.getId());
            assertEquals("CAT-04", dto.getCode());
            assertEquals("Boissons", dto.getDesignation());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
        }

        @Test
        @DisplayName("ne mappe pas la liste articles")
        void shouldNotMapArticles() {
            assertNull(CategoryDto.fromEntity(sampleEntity()).getArticles());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(CategoryDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapAllFields() {
            Category category = CategoryDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(4), category.getId());
            assertEquals("CAT-04", category.getCode());
            assertEquals("Boissons", category.getDesignation());
            assertEquals(Integer.valueOf(9), category.getIdentreprise());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        CategoryDto original = sampleDto();
        CategoryDto result = CategoryDto.fromEntity(CategoryDto.toEntity(original));

        assertEquals(original, result);
    }
}
