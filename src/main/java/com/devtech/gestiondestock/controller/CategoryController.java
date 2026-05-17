package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.CategoryApi;
import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDto save(@RequestBody CategoryDto dto) {
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
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        this.categoryService.delete(id);
    }
}
