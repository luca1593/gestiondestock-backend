package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.*;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.LigneCommandeClient;
import com.devtech.gestiondestock.model.LigneCommandeFournisseur;
import com.devtech.gestiondestock.repository.*;
import com.devtech.gestiondestock.services.ArticleService;
import com.devtech.gestiondestock.validator.ArticleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private LigneVenteRepository venteRepository;
    private LigneCommandeClientRepository commandeClientRepository;
    private LigneCommandeFournisseurRepository commandeFournisseurRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository,
                              LigneVenteRepository venteRepository,
                              LigneCommandeClientRepository commandeClientRepository,
                              LigneCommandeFournisseurRepository commandeFournisseurRepository,
                              CategoryRepository categoryRepository) {
        this.articleRepository = articleRepository;
        this.venteRepository = venteRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ArticleDto save(ArticleDto dto) {
        List<String> errors = ArticleValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Article is not valide {}", dto);
            throw new InvalidEntityException("L'article n'est pas valide", ErrorsCode.ARTICLE_NOT_VALID, errors);
        }
        Optional<Category> category = categoryRepository.findById(dto.getCategory().getId());
        if (!category.isPresent()){
            log.warn("Catehory with ID {} was not found in the DB", dto.getCategory().getId());
            throw new EntityNotFoundException(
                    "La categorie avec l'Id = "
                            + dto.getCategory().getId() +
                            " n'existe pas dans la BDD",
                    ErrorsCode.CATEGORY_NOT_FOUND
            );
        }
        dto.setCategory(CategoryDto.fromEntity(category.get()));
        return ArticleDto.fromEntity(
                articleRepository.save(ArticleDto.toEntity(dto))
        );
    }
    @Override
    public ArticleDto findById(Integer id) {
        checkIdArticle(id);
        Optional<Article> article = articleRepository.findById(id);
        return Optional.of(ArticleDto.fromEntity(article.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun article avec l'ID = " + id + " n'a ete trouver dans la base de donnee",
                        ErrorsCode.ARTICLE_NOT_FOUND
                )
        );
    }

    @Override
    public ArticleDto findByCodeArticle(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Article code is null");
            return null;
        }
        Optional<Article> article = articleRepository.findArticleByCodeArticle(code);
        return Optional.of(ArticleDto.fromEntity(article.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun article avec le code = " + code + " n'a ete trouver dans la base de donnee",
                        ErrorsCode.ARTICLE_NOT_FOUND
                )
        );
    }

    @Override
    public List<ArticleDto> findAll() {
        return articleRepository.findAll().stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneVenteDto> findHistoriqueVente(Integer idArticle) {
        checkIdArticle(idArticle);
        return venteRepository.findAllByArticleId(idArticle)
                .stream().map(LigneVenteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeClientDto> findHistoriqueCommandeClient(Integer idArticle) {
        checkIdArticle(idArticle);
        return commandeClientRepository.findAllByArticleId(idArticle)
                .stream().map(LigneCommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle) {
        checkIdArticle(idArticle);
        return commandeFournisseurRepository.findAllByArticleId(idArticle)
                .stream().map(LigneCommandeFournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDto> findAllByCategorie(Integer idCategorie) {
        if (idCategorie == null){
            log.error("Category ID is null");
            throw new InvalidOpperatioException("Aucun article trouver avec un ID null",
                    ErrorsCode.CATEGORY_NOT_FOUND
            );
        }
        return articleRepository.findAllByCategoryId(idCategorie)
                .stream().map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        checkIdArticle(id);
        articleRepository.deleteById(id);
    }

    private void checkIdArticle(Integer id) {
        if (id == null){
            log.error("Article ID is null");
            throw new EntityNotFoundException(
                    "L'id de l'article est null",
                    ErrorsCode.ID_NOT_VALID
            );
        }
    }
}
