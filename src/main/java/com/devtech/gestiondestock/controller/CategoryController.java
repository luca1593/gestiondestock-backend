package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CategoryApi;
import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luca
 */
@RestController
public class CategoryController implements CategoryApi {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        return this.categoryService.save(dto);
    }

    @Override
    public CategoryDto findById(Integer id) {
        return this.categoryService.findById(id);
    }

    @Override
    public CategoryDto findByCodeCategory(String code) {
        return this.categoryService.findByCodeCategory(code);
    }

    @Override
    public List<CategoryDto> findAll() {
        return this.categoryService.findAll();
    }

    @Override
    public void delete(Integer id) {
        this.categoryService.delete(id);
    }
}
