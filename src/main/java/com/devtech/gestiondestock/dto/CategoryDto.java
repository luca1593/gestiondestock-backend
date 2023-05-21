package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CategoryDto {
    private Integer id;
    private String code;
    private String designation;

    @JsonIgnore
    private List<ArticleDto> articles;
    private Integer identreprise;

    public static CategoryDto fromEntity(Category category){
        if (category == null){
            return null;
        }
        return CategoryDto.builder()
                .id(category.getId())
                .code(category.getCode())
                .designation(category.getDesignation())
                .identreprise(category.getIdentreprise())
                .build();
    }

    public static Category toEntity(CategoryDto categoryDto){
        if(categoryDto == null){
            return  null;
        }
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setCode(categoryDto.getCode());
        category.setDesignation(categoryDto.getDesignation());
        category.setIdentreprise(categoryDto.getIdentreprise());
        return category;
    }
}
