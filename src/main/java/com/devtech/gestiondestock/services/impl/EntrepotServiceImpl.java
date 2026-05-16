package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.repository.EntrepotRepository;
import com.devtech.gestiondestock.services.EntrepotService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntrepotServiceImpl implements EntrepotService {
    private final EntrepotRepository entrepotRepository;

    public EntrepotServiceImpl(EntrepotRepository entrepotRepository) {
        this.entrepotRepository = entrepotRepository;
    }

    @Override
    public EntrepotDto save(EntrepotDto dto) {
        Entrepot entrepot = fromDto(dto);
        Entrepot saved = entrepotRepository.save(entrepot);
        return toDto(saved);
    }

    @Override
    public EntrepotDto findById(Integer id) {
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrepôt non trouvé"));
        return toDto(entrepot);
    }

    @Override
    public List<EntrepotDto> findAll() {
        return entrepotRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EntrepotDto> findByEntreprise(Integer entrepriseId) {
        return entrepotRepository.findByEntrepriseId(entrepriseId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        entrepotRepository.deleteById(id);
    }

    private Entrepot fromDto(EntrepotDto dto) {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(dto.getId());
        entrepot.setCode(dto.getCode());
        entrepot.setNom(dto.getNom());
        entrepot.setDescription(dto.getDescription());
        entrepot.setAdresse(dto.getAdresse());
        entrepot.setEstPrincipal(dto.getEstPrincipal());
        if (dto.getEntrepriseId() != null) {
            Entreprise e = new Entreprise();
            e.setId(dto.getEntrepriseId());
            entrepot.setEntreprise(e);
        }
        return entrepot;
    }

    private EntrepotDto toDto(Entrepot entrepot) {
        EntrepotDto dto = new EntrepotDto();
        dto.setId(entrepot.getId());
        dto.setCode(entrepot.getCode());
        dto.setNom(entrepot.getNom());
        dto.setDescription(entrepot.getDescription());
        dto.setAdresse(entrepot.getAdresse());
        dto.setEstPrincipal(entrepot.getEstPrincipal());
        if (entrepot.getEntreprise() != null) {
            dto.setEntrepriseId(entrepot.getEntreprise().getId());
            dto.setEntrepriseNom(entrepot.getEntreprise().getNom());
        }
        return dto;
    }
}