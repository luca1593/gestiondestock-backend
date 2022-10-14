package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.dto.VenteDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import com.devtech.gestiondestock.repository.VenteRepository;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.services.VenteService;
import com.devtech.gestiondestock.validator.VenteValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VenteServiceImpl implements VenteService {
    private VenteRepository venteRepository;
    private LigneVenteRepository ligneVenteRepository;
    private ArticleRepository articleRepository;

    private MvtStkService mvtStkService;


    @Autowired
    public VenteServiceImpl(VenteRepository venteRepository,
                            LigneVenteRepository ligneVenteRepository,
                            ArticleRepository articleRepository,
                            MvtStkService mvtStkService) {
        this.venteRepository = venteRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.articleRepository = articleRepository;
        this.mvtStkService = mvtStkService;
    }

    @Override
    public VenteDto save(VenteDto dto) {
        List<String> errors = VenteValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Vente is not valide {}", dto);
            throw new InvalidEntityException("Le vente n'est pas valide", ErrorsCode.VENTE_NOT_VALID, errors);
        }
        List<String> venteErrors = new ArrayList<>();

        dto.getLigneVentes().forEach( ligneVente -> {
            Optional<Article> article = articleRepository.findById(
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

        Vente vente = venteRepository.save(VenteDto.toEntity(dto));
        dto.getLigneVentes().forEach(ligneVnt ->{
            LigneVente ligneVente = LigneVenteDto.toEntity(ligneVnt);
            ligneVente.setVente(vente);
            ligneVenteRepository.save(ligneVente);
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
        Optional<Vente> vente = venteRepository.findById(id);
        return Optional.of(VenteDto.fromEntity(vente.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucune vente avec l'ID = " + id + " n'a ete trouver dans la base de donnee",
                        ErrorsCode.VENTE_NOT_FOUND
                )
        );
    }

    @Override
    public VenteDto findByCodeVente(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Category code is null");
            return null;
        }
        Optional<Vente> vente = venteRepository.findVenteByCode(code);
        return Optional.of(VenteDto.fromEntity(vente.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucune vente trouver avec le code = " + code + " dans la BDD",
                        ErrorsCode.VENTE_NOT_FOUND
                )
        );
    }

    @Override
    public List<VenteDto> findByDateVente(Instant dateVente) {
        if (dateVente == null){
            log.error("Vente date is null");
            return null;
        }
        return venteRepository.findVenteByDateVente(dateVente) != null ?
                venteRepository.findVenteByDateVente(dateVente).stream()
                        .map(VenteDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public List<VenteDto> findAll() {
        return venteRepository.findAll().stream()
                .map(VenteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("Vente ID is null");
            return;
        }
        venteRepository.deleteById(id);
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
            mvtStkService.sortieMvtStk(mvtStkDto);
    }
}
