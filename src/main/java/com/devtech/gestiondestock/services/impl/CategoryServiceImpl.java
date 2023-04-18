package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.CategoryDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.CategoryRepository;
import com.devtech.gestiondestock.services.CategoryService;
import com.devtech.gestiondestock.validator.CategoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private ArticleRepository articleRepository;

    @Autowired
    public  CategoryServiceImpl(CategoryRepository categoryRepository, ArticleRepository articleRepository){
        this.categoryRepository = categoryRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        List<String> errors = CategoryValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Category is not valide {}", dto);
            throw new InvalidEntityException("Le category n'est pas valide", ErrorsCode.CATEGORY_NOT_VALID, errors);
        }
        return CategoryDto.fromEntity(
                categoryRepository.save(CategoryDto.toEntity(dto))
        );
    }

    @Override
    public CategoryDto findById(Integer id) {
        checkId(id);
        Optional<Category> category = categoryRepository.findById(id);
        return Optional.of(CategoryDto.fromEntity(category.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun categorie avec l'ID = " + id + " n'a ete trouver dans la base de donnee",
                        ErrorsCode.CATEGORY_NOT_FOUND
                )
        );
    }

    @Override
    public CategoryDto findByCodeCategory(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Category code is null");
            return null;
        }
        Optional<Category> category = categoryRepository.findCategoryByCode(code);
        return Optional.of(CategoryDto.fromEntity(category.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun category trouver avec le code = " + code + " dans la BDD",
                        ErrorsCode.CATEGORY_NOT_FOUND
                )
        );
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        validateIdBefortOperation(id);
        categoryRepository.deleteById(id);
    }

    private void checkId(Integer id){
        if (id == null){
            log.error("Category ID is null");
            throw new EntityNotFoundException(
                "L'ID du category est null", 
                ErrorsCode.CATEGORY_NOT_FOUND
            );
        }
    }

    private void validateIdBefortOperation(Integer id){
        checkId(id);
        List<Article> articles = articleRepository.findAllByCategoryId(id);
        if(!CollectionUtils.isEmpty(articles)){
            log.error("Category alredy used");
            throw new InvalidOpperatioException("La categorie est deja utilse", 
            ErrorsCode.CATEGORY_ALREADY_IN_USE
            );
        }
    }
}
