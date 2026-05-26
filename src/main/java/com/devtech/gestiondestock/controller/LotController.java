package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.LotApi;
import com.devtech.gestiondestock.dto.LotDto;
import com.devtech.gestiondestock.services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
public class LotController implements LotApi {
    private final LotService lotService;

    @Autowired
    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @Override
    public LotDto save(@RequestBody LotDto dto) {
        return lotService.save(dto);
    }

    @Override
    public LotDto findById(Integer id) {
        return lotService.findById(id);
    }

    @Override
    public List<LotDto> findAll() {
        return lotService.findAll();
    }

    @Override
    public List<LotDto> findByArticle(Integer articleId) {
        return lotService.findByArticle(articleId);
    }

    @Override
    public List<LotDto> findByEntrepot(Integer entrepotId) {
        return lotService.findByEntrepot(entrepotId);
    }

    @Override
    public List<LotDto> findAvailable() {
        return lotService.findAvailable();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('Admin', 'ROLE_Admin', 'ADMIN', 'Manager', 'ROLE_Manager', 'MANAGER')")
    public void delete(Integer id) {
        lotService.delete(id);
    }
}
