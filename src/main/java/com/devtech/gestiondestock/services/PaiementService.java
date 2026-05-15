package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.model.Paiement;
import java.util.List;

public interface PaiementService {
    PaiementDto save(PaiementDto dto);
    PaiementDto findById(Integer id);
    List<PaiementDto> findAll();
    List<PaiementDto> findByFacture(Integer factureId);
    List<PaiementDto> findByModePaiement(Paiement.ModePaiement modePaiement);
    void delete(Integer id);
}
