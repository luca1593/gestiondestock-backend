package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AvoirDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.model.Avoir;
import com.devtech.gestiondestock.repository.AvoirRepository;
import com.devtech.gestiondestock.services.AvoirService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
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
        return AvoirDto.fromEntity(
                avoirRepository.save(AvoirDto.toEntity(dto))
        );
    }

    @Override
    public AvoirDto findById(Integer id) {
        Optional<Avoir> avoir = avoirRepository.findById(id);
        return avoir.map(AvoirDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException("Avoir non trouve", ErrorsCode.ARTICLE_NOT_FOUND));
    }

    @Override
    public List<AvoirDto> findByClientId(Integer clientId, Integer identreprise) {
        return avoirRepository.findByClientIdAndIdentreprise(clientId, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AvoirDto> findByVenteId(Integer venteId, Integer identreprise) {
        return avoirRepository.findByVenteIdAndIdentreprise(venteId, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AvoirDto> findByEtat(String etat, Integer identreprise) {
        return avoirRepository.findByEtatAndIdentreprise(etat, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AvoirDto> findByDateRange(Instant debut, Instant fin, Integer identreprise) {
        return avoirRepository.findByDateRange(debut, fin, identreprise)
                .stream().map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AvoirDto> findAll(Integer identreprise) {
        return avoirRepository.findAll().stream()
                .filter(a -> a.getIdentreprise() == null || a.getIdentreprise().equals(identreprise))
                .map(AvoirDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        avoirRepository.deleteById(id);
    }
}
