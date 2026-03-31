package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.AlertStockDto;

import java.util.List;

public interface AlertStockService {
    AlertStockDto save(AlertStockDto dto);
    AlertStockDto findById(Integer id);
    List<AlertStockDto> findAllByEntreprise(Integer identreprise);
    List<AlertStockDto> findAlertesActives(Integer identreprise);
    List<AlertStockDto> findByNiveauAlerte(String niveauAlerte);
    void delete(Integer id);
    void checkAndCreateAlerts(Integer identreprise);
}
