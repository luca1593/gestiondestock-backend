package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.MvtStkApi;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.services.MvtStkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * @author luca
 */
@RestController
public class MvtStkController implements MvtStkApi {
    private MvtStkService mvtStkService;

    @Autowired
    public MvtStkController(MvtStkService mvtStkService) {
        this.mvtStkService = mvtStkService;
    }

    @Override
    public MvtStkDto save(MvtStkDto dto) {
        return this.mvtStkService.save(dto);
    }

    @Override
    public MvtStkDto findById(Integer id) {
        return this.mvtStkService.findById(id);
    }

    @Override
    public List<MvtStkDto> findMvtStkByDateMvt(Instant dateMvt) {
        return this.mvtStkService.findMvtStkByDateMvt(dateMvt);
    }

    @Override
    public List<MvtStkDto> findMvtStkByType(String typeMvt) {
        return this.mvtStkService.findMvtStkByType(typeMvt);
    }

    @Override
    public List<MvtStkDto> findAll() {
        return this.mvtStkService.findAll();
    }

    @Override
    public BigDecimal stockReelArticle(Integer idArticle) {
        return this.mvtStkService.stockReelArticle(idArticle);
    }

    @Override
    public List<MvtStkDto> mvtStkArticle(Integer idArticle) {
        return this.mvtStkService.mvtStkArticle(idArticle);
    }

    @Override
    public MvtStkDto entreMvtStk(MvtStkDto dto) {
        return this.mvtStkService.entreMvtStk(dto);
    }

    @Override
    public MvtStkDto sortieMvtStk(MvtStkDto dto) {
        return this.mvtStkService.sortieMvtStk(dto);
    }

    @Override
    public MvtStkDto correctionMvtStkPos(MvtStkDto dto) {
        return this.mvtStkService.correctionMvtStkPos(dto);
    }

    @Override
    public MvtStkDto correctionMvtStkNeg(MvtStkDto dto) {
        return this.mvtStkService.correctionMvtStkNeg(dto);
    }

    @Override
    public void delete(Integer id) {
        this.mvtStkService.delete(id);
    }
}
