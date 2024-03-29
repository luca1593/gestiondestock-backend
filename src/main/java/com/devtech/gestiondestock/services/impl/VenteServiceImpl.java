package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.dto.VenteDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import com.devtech.gestiondestock.repository.VenteRepository;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.services.VenteService;
import com.devtech.gestiondestock.validator.VenteValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class VenteServiceImpl implements VenteService {
    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final ArticleRepository articleRepository;
    private final MvtStkService mvtStkService;

    @Override
    public VenteDto save(VenteDto dto) {
        dto.setDateVente(Instant.now());
        List<String> errors = VenteValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Vente is not valide {}", dto);
            throw new InvalidEntityException("Le vente n'est pas valide", ErrorsCode.VENTE_NOT_VALID, errors);
        }
        List<String> venteErrors = new ArrayList<>();

        dto.getLigneVentes().forEach( ligneVente -> {
            Optional<Article> article = this.articleRepository.findById(
                    ligneVente.getArticle().getId()
            );
            if(!article.isPresent()){
                venteErrors.add("L'article avec l'Id = "
                        + ligneVente.getArticle().getId() +
                        " n'existe pas dans la BDD"
                );
            }
        });

        if (!venteErrors.isEmpty()){
            log.error("Vente not found in DB, {} ", errors);
            throw new InvalidEntityException(
                    "Un ou plusieur article n'existe pas dans la BDD",
                    ErrorsCode.VENTE_NOT_VALID, venteErrors);
        }

        Vente vente = this.venteRepository.save(VenteDto.toEntity(dto));
        dto.getLigneVentes().forEach(ligneVnt ->{
            LigneVente ligneVente = LigneVenteDto.toEntity(ligneVnt);
            ligneVente.setVente(vente);
            ligneVente.setIdentreprise(dto.getIdentreprise());
            this.ligneVenteRepository.save(ligneVente);
            updateMvtStk(ligneVente);
        });

        return VenteDto.fromEntity(vente);
    }

    @Override
    public VenteDto findById(Integer id) {
        if (id == null){
            log.error("Vente ID is null");
            return null;
        }
        Optional<Vente> vente = this.venteRepository.findById(id);
        if (vente.isPresent()){
            return VenteDto.fromEntity(vente.get());
        }else {
            throw new EntityNotFoundException(
                    "Aucune vente avec l'ID = " + id + " n'a ete trouver dans la base de donnee",
                    ErrorsCode.VENTE_NOT_FOUND
            );
        }
    }

    @Override
    public VenteDto findByCodeVente(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Category code is null");
            return null;
        }
        Optional<Vente> vente = this.venteRepository.findVenteByCode(code);
        if (vente.isPresent()){
            return VenteDto.fromEntity(vente.get());
        }else {
            throw new EntityNotFoundException(
                    "Aucune vente trouver avec le code = " + code + " n'a ete trouver dans la base de donnee",
                    ErrorsCode.VENTE_NOT_FOUND
            );
        }
    }

    @Override
    public List<VenteDto> findByDateVente(Instant dateVente) {
        if (dateVente == null){
            log.error("Vente date is null");
            return Collections.emptyList();
        }
        return this.venteRepository.findVenteByDateVente(dateVente) != null ?
                this.venteRepository.findVenteByDateVente(dateVente).stream()
                        .map(VenteDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public List<VenteDto> findAll() {
        return this.venteRepository.findAll().stream()
                .map(VenteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        checkIdVenteBeforeDelete(id);
        this.venteRepository.deleteById(id);
    }

    @Override
    public List<LigneVenteDto> findAllLigneVenteByVente(Integer idVente) {
        checkIdVenteBeforeDelete(idVente);
        return this.ligneVenteRepository.findAllByVenteId(idVente) != null ?
                this.ligneVenteRepository.findAllByVenteId(idVente).stream()
                        .map(LigneVenteDto::fromEntity).collect(Collectors.toList()) :  new ArrayList<>();
    }

    @Override
    public List<LigneVenteDto> findAllByDateInterval(Instant startDate, Instant endDate) {
        List<LigneVenteDto> ligneVenteDtoList = new ArrayList<>();
        List<Vente> venteList = this.venteRepository.getAllBetweenDates(startDate, endDate);
        venteList.forEach(vnt -> ligneVenteDtoList.addAll(findAllLigneVenteByVente(vnt.getId())));
        return ligneVenteDtoList;
    }

    private void updateMvtStk(LigneVente ligneVente){
            MvtStkDto mvtStkDto = MvtStkDto.builder()
                    .article(ArticleDto.fromEntity(ligneVente.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvt(TypeMvt.SORTIE)
                    .sourceMvt(SourceMvtStk.VENTE)
                    .quantite(ligneVente.getQuantite())
                    .identreprise(ligneVente.getIdentreprise())
                    .build();
        Optional<Article> article = this.articleRepository.findById(ligneVente.getArticle().getId());
        if(article.isPresent()){
            Article art = article.get();
            BigDecimal newStock = art.getStock().subtract(ligneVente.getQuantite());
            art.setStock(newStock);
            this.articleRepository.save(art);
        }
        this.mvtStkService.sortieMvtStk(mvtStkDto);
    }

    private void checkIdVenteBeforeDelete(Integer idVente){
        VenteDto dto = findById(idVente);
        if (!CollectionUtils.isEmpty(dto.getLigneVentes())) {
            log.error("Vente alredy used");
            throw new InvalidOpperatioException("Operation impossible : une ou plusieur line de vente existe deja pour ce vente",
                    ErrorsCode.FOURNISSEUR_ALREADY_IN_USE
            );
        }
    }
}
