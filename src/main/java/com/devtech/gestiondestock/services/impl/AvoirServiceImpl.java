package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.AvoirDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Avoir;
import com.devtech.gestiondestock.model.LigneVente;
import com.devtech.gestiondestock.model.SourceMvtStk;
import com.devtech.gestiondestock.model.TypeMvt;
import com.devtech.gestiondestock.repository.AvoirRepository;
import com.devtech.gestiondestock.repository.LigneVenteRepository;
import com.devtech.gestiondestock.services.AvoirService;
import com.devtech.gestiondestock.services.MvtStkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AvoirServiceImpl implements AvoirService {

    private final AvoirRepository avoirRepository;
    private final MvtStkService mvtStkService;
    private final LigneVenteRepository ligneVenteRepository;

    @Override
    @Transactional
    public AvoirDto save(AvoirDto dto) {
        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            dto.setCode("AVOIR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (dto.getDateAvoir() == null) {
            dto.setDateAvoir(Instant.now());
        }
        if (dto.getEtat() == null) {
            dto.setEtat("EN_ATTENTE");
        }
        AvoirDto saved = AvoirDto.fromEntity(
                avoirRepository.save(AvoirDto.toEntity(dto))
        );
        if ("VALIDE".equals(saved.getEtat()) && saved.getVente() != null && saved.getVente().getId() != null) {
            creerMvtStkPourAvoir(saved.getVente().getId(), saved.getIdentreprise());
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public AvoirDto findById(Integer id) {
        Optional<Avoir> avoir = avoirRepository.findById(id);
        return avoir.map(AvoirDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException("Avoir non trouve", ErrorsCode.ARTICLE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvoirDto> findByClientId(Integer clientId, Integer identreprise) {
        return avoirRepository.findByClientIdAndIdentreprise(clientId, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvoirDto> findByVenteId(Integer venteId, Integer identreprise) {
        return avoirRepository.findByVenteIdAndIdentreprise(venteId, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvoirDto> findByEtat(String etat, Integer identreprise) {
        return avoirRepository.findByEtatAndIdentreprise(etat, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvoirDto> findByDateRange(Instant debut, Instant fin, Integer identreprise) {
        return avoirRepository.findByDateRange(debut, fin, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvoirDto> findAll(Integer identreprise) {
        return avoirRepository.findAll().stream()
                .filter(a -> a.getIdentreprise() == null || a.getIdentreprise().equals(identreprise))
                .map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvoirDto updateEtat(Integer id, String etat) {
        Optional<Avoir> optionalAvoir = avoirRepository.findById(id);
        if (optionalAvoir.isEmpty()) {
            throw new EntityNotFoundException("Avoir non trouve avec l'ID " + id, ErrorsCode.ARTICLE_NOT_FOUND);
        }
        Avoir avoir = optionalAvoir.get();
        if (avoir.getVente() == null || avoir.getVente().getId() == null) {
            throw new InvalidEntityException("L'avoir n'est pas lie a une vente", ErrorsCode.ARTICLE_NOT_FOUND);
        }
        String ancienEtat = avoir.getEtat();
        avoir.setEtat(etat);
        Avoir saved = avoirRepository.save(avoir);

        if ("VALIDE".equals(etat) && !"VALIDE".equals(ancienEtat)) {
            creerMvtStkPourAvoir(saved.getVente().getId(), saved.getIdentreprise());
        }

        return AvoirDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        avoirRepository.deleteById(id);
    }

    private void creerMvtStkPourAvoir(Integer venteId, Integer identreprise) {
        List<LigneVente> lignes = ligneVenteRepository.findAllByVenteId(venteId);
        for (LigneVente ligne : lignes) {
            if (ligne.getArticle() != null && ligne.getArticle().getId() != null
                    && ligne.getQuantite() != null && ligne.getQuantite().signum() > 0) {
                MvtStkDto mvtDto = MvtStkDto.builder()
                        .dateMvt(Instant.now())
                        .quantite(ligne.getQuantite())
                        .article(ArticleDto.fromEntity(ligne.getArticle()))
                        .typeMvt(TypeMvt.ENTRER)
                        .sourceMvt(SourceMvtStk.AVOIR)
                        .identreprise(identreprise)
                        .build();
                mvtStkService.entreMvtStk(mvtDto);
            }
        }
    }
}
