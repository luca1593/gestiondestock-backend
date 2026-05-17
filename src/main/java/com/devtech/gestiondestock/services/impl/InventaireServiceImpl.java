package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.dto.InventaireLigneDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.InventaireLigneRepository;
import com.devtech.gestiondestock.repository.InventaireRepository;
import com.devtech.gestiondestock.services.InventaireService;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.validator.InventaireValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventaireServiceImpl implements InventaireService {
    private static final Logger log = LoggerFactory.getLogger(InventaireServiceImpl.class);
    private final InventaireRepository inventaireRepository;
    private final InventaireLigneRepository inventaireLigneRepository;
    private final MvtStkService mvtStkService;

    public InventaireServiceImpl(InventaireRepository inventaireRepository, InventaireLigneRepository inventaireLigneRepository, MvtStkService mvtStkService) {
        this.inventaireRepository = inventaireRepository;
        this.inventaireLigneRepository = inventaireLigneRepository;
        this.mvtStkService = mvtStkService;
    }

    @Override
    @Transactional
    public InventaireDto save(InventaireDto dto) {
        List<String> errors = InventaireValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Inventaire is not valid {}", dto);
            throw new InvalidEntityException("L'inventaire n'est pas valide", ErrorsCode.INVENTAIRE_NOT_VALID, errors);
        }

        boolean newStatutTermine = Inventaire.Statut.TERMINE.equals(dto.getStatut());
        boolean alreadyTermine = false;

        if (dto.getId() != null) {
            Optional<Inventaire> existing = inventaireRepository.findById(dto.getId());
            alreadyTermine = existing.isPresent() && Inventaire.Statut.TERMINE.equals(existing.get().getStatut());
        }

        Inventaire inventaire = InventaireDto.toEntity(dto);
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            inventaire.setEntreprise(e);
        }
        if (dto.getEntrepotId() != null) {
            Entrepot e = new Entrepot();
            e.setId(dto.getEntrepotId());
            inventaire.setEntrepot(e);
        }

        Inventaire saved = inventaireRepository.save(inventaire);

        if (dto.getLignes() != null) {
            inventaireLigneRepository.findByInventaireId(saved.getId()).forEach(l -> inventaireLigneRepository.delete(l));
            for (InventaireLigneDto ligneDto : dto.getLignes()) {
                InventaireLigne ligne = InventaireLigneDto.toEntity(ligneDto);
                ligne.setInventaire(saved);
                if (ligneDto.getArticleId() != null) {
                    Article article = new Article();
                    article.setId(ligneDto.getArticleId());
                    ligne.setArticle(article);
                }
                inventaireLigneRepository.save(ligne);
            }
        }

        if (newStatutTermine && !alreadyTermine && dto.getLignes() != null) {
            for (InventaireLigneDto ligneDto : dto.getLignes()) {
                BigDecimal theorique = ligneDto.getQuantiteTheorique() != null ? ligneDto.getQuantiteTheorique() : BigDecimal.ZERO;
                BigDecimal reelle = ligneDto.getQuantiteReelle() != null ? ligneDto.getQuantiteReelle() : BigDecimal.ZERO;
                BigDecimal diff = reelle.subtract(theorique);

                if (diff.signum() != 0 && ligneDto.getArticleId() != null) {
                    MvtStkDto mvtDto = MvtStkDto.builder()
                            .dateMvt(Instant.now())
                            .quantite(diff.abs())
                            .article(ArticleDto.builder().id(ligneDto.getArticleId()).build())
                            .typeMvt(diff.signum() > 0 ? TypeMvt.CORRECTION_POS : TypeMvt.CORRECTION_NEG)
                            .sourceMvt(SourceMvtStk.INVENTAIRE)
                            .identreprise(dto.getEntrepriseId())
                            .build();
                    if (diff.signum() > 0) {
                        mvtStkService.correctionMvtStkPos(mvtDto);
                    } else {
                        mvtStkService.correctionMvtStkNeg(mvtDto);
                    }
                }
            }
        }

        return InventaireDto.fromEntity(inventaireRepository.findById(saved.getId()).orElse(saved));
    }

    @Override
    public InventaireDto findById(Integer id) {
        return inventaireRepository.findById(id)
                .map(InventaireDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Inventaire non trouvé avec l'ID " + id, ErrorsCode.INVENTAIRE_NOT_FOUND));
    }

    @Override
    public InventaireDto findByCode(String code) {
        Inventaire inv = inventaireRepository.findByCode(code);
        if (inv == null) throw new EntityNotFoundException("Inventaire non trouvé avec le code " + code, ErrorsCode.INVENTAIRE_NOT_FOUND);
        return InventaireDto.fromEntity(inv);
    }

    @Override
    public List<InventaireDto> findAll() {
        return inventaireRepository.findAll().stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByEntreprise(Integer entrepriseId) {
        return inventaireRepository.findByEntrepriseId(entrepriseId).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByEntrepot(Integer entrepotId) {
        return inventaireRepository.findByEntrepotId(entrepotId).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<InventaireDto> findByStatut(Inventaire.Statut statut) {
        return inventaireRepository.findByStatut(statut).stream().map(InventaireDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        inventaireLigneRepository.findByInventaireId(id).forEach(l -> inventaireLigneRepository.delete(l));
        inventaireRepository.deleteById(id);
    }
}
