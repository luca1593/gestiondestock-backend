package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.TransfertStockDto;
import com.devtech.gestiondestock.model.TransfertStock;
import java.util.List;

public interface TransfertStockService {
    TransfertStockDto save(TransfertStockDto dto);
    TransfertStockDto findById(Integer id);
    TransfertStockDto findByCode(String code);
    List<TransfertStockDto> findAll();
    List<TransfertStockDto> findByEntreprise(Integer entrepriseId);
    List<TransfertStockDto> findByStatut(TransfertStock.StatutTransfert statut);
    List<TransfertStockDto> findByEntrepotSource(Integer entrepotId);
    List<TransfertStockDto> findByEntrepotDestination(Integer entrepotId);
    void delete(Integer id);
}
