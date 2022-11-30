package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.config.H2JpaConfig;
import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class CategoryServiceImplTest {
    @Autowired
    private CategoryService categoryService;

    @Test
    public void shouldSaveCategoryWithSuccess(){
       /* CategoryDto expectedCategorie = CategoryDto.builder()
                .code("Cat-test")
                .designation("Designation test")
                .identreprise(1)
                .build();

        CategoryDto savedCategory = categoryService.save(expectedCategorie);

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());
        assertEquals(expectedCategorie.getCode(), savedCategory.getCode());
        assertEquals(expectedCategorie.getDesignation(), savedCategory.getDesignation());
        assertEquals(expectedCategorie.getId(), savedCategory.getId());*/
    }

}