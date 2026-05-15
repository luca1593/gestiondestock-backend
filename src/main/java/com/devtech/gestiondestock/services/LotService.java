package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.LotDto;
import java.time.LocalDateTime;
import java.util.List;

public interface LotService {
    LotDto save(LotDto dto);
    LotDto findById(Integer id);
    LotDto findByNumeroLot(String numeroLot);
    List<LotDto> findAll();
    List<LotDto> findByArticle(Integer articleId);
    List<LotDto> findByEntrepot(Integer entrepotId);
    List<LotDto> findExpiredBefore(LocalDateTime date);
    List<LotDto> findAvailable();
    void delete(Integer id);
}
