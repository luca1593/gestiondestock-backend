package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.MvtStk;
import com.devtech.gestiondestock.model.TypeMvt;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.MvtStkRepository;
import com.devtech.gestiondestock.services.AlertStockService;
import com.devtech.gestiondestock.services.ArticleService;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.validator.MvtStkValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MvtStkServiceImpl implements MvtStkService {

    private final MvtStkRepository mvtStkRepository;
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;
    private final AlertStockService alertStockService;

    @Autowired
    public MvtStkServiceImpl(MvtStkRepository mvtStkRepository, ArticleService articleService, ArticleRepository articleRepository, AlertStockService alertStockService) {
        this.mvtStkRepository = mvtStkRepository;
        this.articleService = articleService;
        this.articleRepository = articleRepository;
        this.alertStockService = alertStockService;
    }

    @Override
    public MvtStkDto save(MvtStkDto dto) {
        List<String> errors = MvtStkValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Mouvement is not valid: {}", dto);
            throw new InvalidEntityException("Le mouvement n'est pas valide", ErrorsCode.MOUVEMENT_STOCK_NOT_VALID, errors);
        }
        return MvtStkDto.fromEntity(
                this.mvtStkRepository.save(MvtStkDto.toEntity(dto))
        );
    }

    @Override
    public MvtStkDto findById(Integer id) {
        if (id == null){
            log.error("Mouvement ID is null");
            return null;
        }
        Optional<MvtStk> mvtStk = this.mvtStkRepository.findById(id);
        return Optional.of(MvtStkDto.fromEntity(mvtStk.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun mouvement trouver avec l'id = " + id + " dans la BDD",
                        ErrorsCode.MOUVEMENT_STOCK_NOT_FOUND
                )
        );
    }

    @Override
    public List<MvtStkDto> findMvtStkByDateMvt(Instant dateMvt) {
        if (dateMvt == null) {
            log.error("Mouvement Date is null");
            return null;
        }
        return this.mvtStkRepository.findMvtStkByDateMvt(dateMvt) != null ?
                this.mvtStkRepository.findMvtStkByDateMvt(dateMvt)
                        .stream()
                        .map(MvtStkDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public List<MvtStkDto> findMvtStkByType(String typeMvt) {
        if (!StringUtils.hasLength(typeMvt)) {
            log.error("Mouvement Type is null");
            return null;
        }
        try {
            TypeMvt type = TypeMvt.valueOf(typeMvt);
            return this.mvtStkRepository.findMvtStkByTypeMvt(type) != null ?
                    this.mvtStkRepository.findMvtStkByTypeMvt(type)
                            .stream()
                            .map(MvtStkDto::fromEntity)
                            .collect(Collectors.toList()) : null;
        } catch (IllegalArgumentException e) {
            log.error("Invalid movement type: {}", typeMvt);
            return null;
        }
    }

    @Override
    public List<MvtStkDto> findAll() {
        return this.mvtStkRepository.findAll()
                .stream().map(MvtStkDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal stockReelArticle(Integer idArticle) {
        if (!checkIdArticle(idArticle)) {
            return BigDecimal.valueOf(-1);
        }
        this.articleService.findById(idArticle);
        return this.mvtStkRepository.stockReelArticle(idArticle);
    }

    @Override
    public List<MvtStkDto> mvtStkArticle(Integer idArticle) {
        if (!checkIdArticle(idArticle)) {
            return null;
        }
        return this.mvtStkRepository.findAllByArticleId(idArticle) != null ?
                this.mvtStkRepository.findAllByArticleId(idArticle)
                        .stream().map(MvtStkDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public MvtStkDto entreMvtStk(MvtStkDto dto) {
        return getMvtStkDto(dto,
                Math.abs(dto.getQuantite().doubleValue()),
                TypeMvt.ENTRER, ErrorsCode.MOUVEMENT_STOCK_NOT_VALID);
    }

    @Override
    public MvtStkDto sortieMvtStk(MvtStkDto dto) {
        return getMvtStkDto(dto,
                Math.abs(dto.getQuantite().doubleValue()) * -1,
                TypeMvt.SORTIE, ErrorsCode.MOUVEMENT_STOCK_NOT_VALID);
    }

    @Override
    public MvtStkDto correctionMvtStkPos(MvtStkDto dto) {
        return getMvtStkDto(dto,
                Math.abs(dto.getQuantite().doubleValue()),
                TypeMvt.CORRECTION_POS, ErrorsCode.MOUVEMENT_STOCK_NOT_VALID);
    }

    @Override
    public MvtStkDto correctionMvtStkNeg(MvtStkDto dto) {
        return getMvtStkDto(dto,
                Math.abs(dto.getQuantite().doubleValue()) * -1,
                TypeMvt.CORRECTION_NEG, ErrorsCode.MOUVEMENT_STOCK_NOT_VALID);
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("Mouvement ID is null");
            return;
        }
        this.mvtStkRepository.deleteById(id);
    }

    private Boolean checkIdArticle(Integer idArticle) {
        if (idArticle == null){
            log.warn("ID article is null");
            return false;
        }
        return true;
    }

    private MvtStkDto getMvtStkDto(MvtStkDto dto, double quantite, TypeMvt typeMvt, ErrorsCode errorsCode) {
        dto.setTypeMvt(typeMvt);
        List<String> errors = MvtStkValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Mouvement de stock is not valid: {}", dto);
            throw new InvalidEntityException("Le mouvement de stock n'est pas valide",
                    errorsCode, errors);
        }
        dto.setQuantite(BigDecimal.valueOf(quantite));
        MvtStkDto saved = MvtStkDto.fromEntity(
                this.mvtStkRepository.save(MvtStkDto.toEntity(dto))
        );
        if (dto.getArticle() != null && dto.getArticle().getId() != null) {
            this.articleRepository.findById(dto.getArticle().getId()).ifPresent(article -> {
                BigDecimal currentStock = article.getStock() != null ? article.getStock() : BigDecimal.ZERO;
                article.setStock(currentStock.add(BigDecimal.valueOf(quantite)));
                this.articleRepository.save(article);
            });
            if (dto.getIdentreprise() != null) {
                this.alertStockService.checkAndCreateAlerts(dto.getIdentreprise());
            }
        }
        return saved;
    }
}
