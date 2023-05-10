package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.MvtStkDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface MvtStkService {
    MvtStkDto save(MvtStkDto dto);
    MvtStkDto findById(Integer id);
    List<MvtStkDto> findMvtStkByDateMvt(Instant dateMvt);
    List<MvtStkDto> findMvtStkByType(String typeMvt);
    List<MvtStkDto> findAll();

    BigDecimal stockReelArticle(Integer idArticle);
    List<MvtStkDto> mvtStkArticle(Integer idArticle);
    MvtStkDto entreMvtStk(MvtStkDto dto);
    MvtStkDto sortieMvtStk(MvtStkDto dto);
    MvtStkDto correctionMvtStkPos(MvtStkDto dto);
    MvtStkDto correctionMvtStkNeg(MvtStkDto dto);


    void delete(Integer id);

}
