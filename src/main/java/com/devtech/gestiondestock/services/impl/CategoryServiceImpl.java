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
import org.slf4j.MDC;
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

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

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
        resoudreEntreprise(dto);
        checkCodeCategoryUniqueDansEntreprise(dto);
        return CategoryDto.fromEntity(
                this.categoryRepository.save(CategoryDto.toEntity(dto))
        );
    }

    /**
     * L'entreprise d'une categorie est determinee cote serveur, jamais par le client :
     * <ul>
     *   <li>corps sans entreprise -&gt; on reprend celle du jeton (MDC) ;</li>
     *   <li>corps avec une entreprise differente de celle du jeton -&gt; <b>400</b>
     *       ({@code CROSS_TENANT_NOT_ALLOWED}). Sans ce refus, un utilisateur pourrait
     *       rattacher ses donnees a une autre entreprise (ecriture inter-tenant) et
     *       contourner la regle d'unicite du code par entreprise.</li>
     * </ul>
     * En contexte systeme (pas de jeton, ex. onboarding d'entreprise), aucune contrainte
     * n'est appliquee : il n'y a pas d'entreprise de reference.
     */
    private void resoudreEntreprise(CategoryDto dto) {
        Integer entrepriseDuJeton = entrepriseCourante();
        if (entrepriseDuJeton == null) {
            return;
        }
        if (dto.getIdentreprise() == null) {
            dto.setIdentreprise(entrepriseDuJeton);
            return;
        }
        if (!entrepriseDuJeton.equals(dto.getIdentreprise())) {
            log.error("Ecriture inter-entreprise refusee : entreprise du jeton = {}, corps = {}",
                    entrepriseDuJeton, dto.getIdentreprise());
            throw new InvalidOpperatioException(
                    "La categorie doit appartenir a votre entreprise",
                    ErrorsCode.CROSS_TENANT_NOT_ALLOWED
            );
        }
    }

    /** Entreprise du jeton authentifie, ou {@code null} en contexte systeme. */
    private Integer entrepriseCourante() {
        String idEntreprise = MDC.get("idEntreprise");
        if (!StringUtils.hasText(idEntreprise)) {
            return null;
        }
        try {
            return Integer.valueOf(idEntreprise.trim());
        } catch (NumberFormatException e) {
            log.warn("Identifiant d'entreprise invalide dans le contexte : {}", idEntreprise);
            return null;
        }
    }

    /**
     * Le code categorie doit etre unique AU SEIN de l'entreprise (deux entreprises
     * peuvent utiliser le meme code). La requete est automatiquement scopee a
     * l'entreprise courante par l'intercepteur multi-tenant.
     * En modification (dto.id renseigne), la categorie elle-meme est exclue du controle.
     */
    private void checkCodeCategoryUniqueDansEntreprise(CategoryDto dto) {
        List<Category> existantes = this.categoryRepository.findAllByCode(dto.getCode());
        boolean doublon = existantes.stream()
                .anyMatch(c -> !java.util.Objects.equals(c.getId(), dto.getId()));
        if (doublon) {
            log.error("Code categorie deja utilise dans l'entreprise : {}", dto.getCode());
            throw new InvalidEntityException(
                    "Une categorie avec le code " + dto.getCode()
                            + " existe deja dans votre entreprise",
                    ErrorsCode.CATEGORY_CODE_ALREADY_EXISTS,
                    List.of("Le code de la categorie doit etre unique dans l'entreprise")
            );
        }
    }

    @Override
    public CategoryDto findById(Integer id) {
        checkId(id);
        return this.categoryRepository.findById(id)
                .map(CategoryDto::fromEntity)
                .orElseThrow(() ->
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
        return this.categoryRepository.findCategoryByCode(code)
                .map(CategoryDto::fromEntity)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Aucun category trouver avec le code = " + code + " dans la BDD",
                                ErrorsCode.CATEGORY_NOT_FOUND
                        )
                );
    }

    @Override
    public List<CategoryDto> findAll() {
        return this.categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        validateIdBefortOperation(id);
        this.categoryRepository.deleteById(id);
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
        List<Article> articles = this.articleRepository.findAllByCategoryId(id);
        if(!CollectionUtils.isEmpty(articles)){
            log.error("Category already used");
            throw new InvalidOpperatioException("La categorie est deja utilisee", 
            ErrorsCode.CATEGORY_ALREADY_IN_USE
            );
        }
    }
}
