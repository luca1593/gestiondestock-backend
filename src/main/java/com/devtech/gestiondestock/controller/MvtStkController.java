package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.MvtStkApi;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.services.MvtStkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
public class MvtStkController implements MvtStkApi {
    private MvtStkService mvtStkService;

    @Autowired
    public MvtStkController(MvtStkService mvtStkService) {
        this.mvtStkService = mvtStkService;
    }

    @Override
    public MvtStkDto save(MvtStkDto dto) {
        return mvtStkService.save(dto);
    }

    @Override
    public MvtStkDto findById(Integer id) {
        return mvtStkService.findById(id);
    }

    @Override
    public List<MvtStkDto> findMvtStkByDateMvt(Instant dateMvt) {
        return mvtStkService.findMvtStkByDateMvt(dateMvt);
    }

    @Override
    public List<MvtStkDto> findMvtStkByType(String typeMvt) {
        return mvtStkService.findMvtStkByType(typeMvt);
    }

    @Override
    public List<MvtStkDto> findAll() {
        return mvtStkService.findAll();
    }

    @Override
    public BigDecimal stockReelArticle(Integer idArticle) {
        return mvtStkService.stockReelArticle(idArticle);
    }

    @Override
    public List<MvtStkDto> mvtStkArticle(Integer idArticle) {
        return mvtStkService.mvtStkArticle(idArticle);
    }

    @Override
    public MvtStkDto entreMvtStk(MvtStkDto dto) {
        return mvtStkService.entreMvtStk(dto);
    }

    @Override
    public MvtStkDto sortieMvtStk(MvtStkDto dto) {
        return mvtStkService.sortieMvtStk(dto);
    }

    @Override
    public MvtStkDto correctionMvtStkPos(MvtStkDto dto) {
        return mvtStkService.correctionMvtStkPos(dto);
    }

    @Override
    public MvtStkDto correctionMvtStkNeg(MvtStkDto dto) {
        return mvtStkService.correctionMvtStkNeg(dto);
    }

    @Override
    public void delete(Integer id) {
        mvtStkService.delete(id);
    }
}
