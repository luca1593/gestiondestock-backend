package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link CategoryServiceImpl}.
 * On verrouille notamment la regle d'unicite du code categorie au sein d'une entreprise.
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ArticleRepository articleRepository;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, articleRepository);
    }

    private CategoryDto categoryDto() {
        return CategoryDto.builder()
                .code("CAT-001")
                .designation("Categorie test")
                .identreprise(1)
                .build();
    }

    @Test
    void save_whenCodeManquant_shouldThrowInvalidEntityException() {
        CategoryDto dto = CategoryDto.builder().designation("sans code").build();

        assertThrows(InvalidEntityException.class, () -> categoryService.save(dto));

        verifyNoInteractions(categoryRepository);
    }

    @Test
    void save_whenCodeCategorieDejaExisteDansLEntreprise_shouldThrowInvalidEntityException() {
        CategoryDto dto = categoryDto();
        Category existante = new Category();
        existante.setId(7);
        existante.setCode("CAT-001");
        when(categoryRepository.findAllByCode("CAT-001")).thenReturn(List.of(existante));

        InvalidEntityException ex = assertThrows(InvalidEntityException.class, () -> categoryService.save(dto));

        assertEquals(ErrorsCode.CATEGORY_CODE_ALREADY_EXISTS, ex.getErrorsCode());
        assertTrue(ex.getMessage().contains("CAT-001"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void save_whenMemeCategorieModifieeSansChangerLeCode_shouldNotThrow() {
        CategoryDto dto = categoryDto();
        dto.setId(7);

        Category elleMeme = new Category();
        elleMeme.setId(7);
        elleMeme.setCode("CAT-001");
        when(categoryRepository.findAllByCode("CAT-001")).thenReturn(List.of(elleMeme));
        when(categoryRepository.save(any(Category.class))).thenReturn(elleMeme);

        CategoryDto result = categoryService.save(dto);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void save_whenCodeLibre_shouldSaveCategory() {
        CategoryDto dto = categoryDto();

        Category enregistree = new Category();
        enregistree.setId(11);
        enregistree.setCode("CAT-001");
        when(categoryRepository.findAllByCode("CAT-001")).thenReturn(List.of());
        when(categoryRepository.save(any(Category.class))).thenReturn(enregistree);

        CategoryDto result = categoryService.save(dto);

        assertNotNull(result);
        assertEquals(11, result.getId());
        assertEquals("CAT-001", result.getCode());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void save_sansIdentreprise_doitReprendreLentrepriseDuJeton() {
        MDC.put("idEntreprise", "7");
        try {
            CategoryDto dto = CategoryDto.builder().code("CAT-TENANT").designation("Tenant").build();
            when(categoryRepository.findAllByCode("CAT-TENANT")).thenReturn(List.of());
            when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

            CategoryDto result = categoryService.save(dto);

            assertEquals(7, result.getIdentreprise());
        } finally {
            MDC.clear();
        }
    }

    @Test
    void save_avecEntrepriseDifferenteDuJeton_doitRefuser() {
        MDC.put("idEntreprise", "1");
        try {
            CategoryDto dto = categoryDto();
            dto.setIdentreprise(2); // tentative d'ecriture inter-entreprise

            InvalidOpperatioException ex = assertThrows(InvalidOpperatioException.class,
                    () -> categoryService.save(dto));

            assertEquals(ErrorsCode.CROSS_TENANT_NOT_ALLOWED, ex.getErrorsCode());
            verify(categoryRepository, never()).save(any(Category.class));
        } finally {
            MDC.clear();
        }
    }

    @Test
    void save_avecEntrepriseDuJeton_doitAccepter() {
        MDC.put("idEntreprise", "1");
        try {
            CategoryDto dto = categoryDto(); // identreprise = 1
            Category enregistree = new Category();
            enregistree.setId(12);
            enregistree.setCode("CAT-001");
            enregistree.setIdentreprise(1);
            when(categoryRepository.findAllByCode("CAT-001")).thenReturn(List.of());
            when(categoryRepository.save(any(Category.class))).thenReturn(enregistree);

            CategoryDto result = categoryService.save(dto);

            assertEquals(1, result.getIdentreprise());
        } finally {
            MDC.clear();
        }
    }
}
