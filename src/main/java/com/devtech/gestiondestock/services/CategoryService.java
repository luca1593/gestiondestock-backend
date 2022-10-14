package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto dto);

    CategoryDto findById(Integer id);

    CategoryDto findByCodeCategory(String code);

    List<CategoryDto> findAll();

    void delete(Integer id);

}
