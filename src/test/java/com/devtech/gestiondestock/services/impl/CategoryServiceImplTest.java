package com.devtech.gestiondestock.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceImplTest {

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