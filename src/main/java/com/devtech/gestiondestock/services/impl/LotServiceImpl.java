package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.LotDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Lot;
import com.devtech.gestiondestock.repository.LotRepository;
import com.devtech.gestiondestock.services.LotService;
import com.devtech.gestiondestock.validator.LotValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LotServiceImpl implements LotService {
    private static final Logger log = LoggerFactory.getLogger(LotServiceImpl.class);
    private final LotRepository lotRepository;

    public LotServiceImpl(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    @Override
    public LotDto save(LotDto dto) {
        List<String> errors = LotValidator.validate(dto);
        if (!errors.isEmpty()) {
            log.error("Lot is not valid {}", dto);
            throw new InvalidEntityException("Le lot n'est pas valide", ErrorsCode.LOT_NOT_VALID, errors);
        }
        Lot lot = LotDto.toEntity(dto);
        if (dto.getArticleId() != null) {
            Article article = new Article();
            article.setId(dto.getArticleId());
            lot.setArticle(article);
        }
        if (dto.getEntrepotId() != null) {
            Entrepot entrepot = new Entrepot();
            entrepot.setId(dto.getEntrepotId());
            lot.setEntrepot(entrepot);
        }
        if (dto.getEntrepriseId() != null) {
            Entreprise entreprise = new Entreprise();
            entreprise.setId(dto.getEntrepriseId());
            lot.setEntreprise(entreprise);
        }
        return LotDto.fromEntity(lotRepository.save(lot));
    }

    @Override
    public LotDto findById(Integer id) {
        return lotRepository.findById(id)
                .map(LotDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Lot non trouvé avec l'ID " + id, ErrorsCode.LOT_NOT_FOUND));
    }

    @Override
    public LotDto findByNumeroLot(String numeroLot) {
        Lot lot = lotRepository.findByNumeroLot(numeroLot);
        if (lot == null) throw new EntityNotFoundException("Lot non trouvé avec le numéro " + numeroLot, ErrorsCode.LOT_NOT_FOUND);
        return LotDto.fromEntity(lot);
    }

    @Override
    public List<LotDto> findAll() {
        return lotRepository.findAll().stream().map(LotDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LotDto> findByArticle(Integer articleId) {
        return lotRepository.findByArticleId(articleId).stream().map(LotDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LotDto> findByEntrepot(Integer entrepotId) {
        return lotRepository.findByEntrepotId(entrepotId).stream().map(LotDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LotDto> findExpiredBefore(LocalDateTime date) {
        return lotRepository.findByDateExpirationBefore(date).stream().map(LotDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LotDto> findAvailable() {
        return lotRepository.findByEstCompletementUtiliseFalse().stream().map(LotDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        lotRepository.deleteById(id);
    }
}
