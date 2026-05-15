package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.EntrepotApi;
import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.services.EntrepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class EntrepotController implements EntrepotApi {
    private final EntrepotService entrepotService;

    @Autowired
    public EntrepotController(EntrepotService entrepotService) {
        this.entrepotService = entrepotService;
    }

    @Override
    public EntrepotDto save(EntrepotDto dto) {
        return entrepotService.save(dto);
    }

    @Override
    public EntrepotDto findById(Integer id) {
        return entrepotService.findById(id);
    }

    @Override
    public List<EntrepotDto> findAll() {
        return entrepotService.findAll();
    }

    @Override
    public List<EntrepotDto> findByEntreprise(Integer entrepriseId) {
        return entrepotService.findByEntreprise(entrepriseId);
    }

    @Override
    public void delete(Integer id) {
        entrepotService.delete(id);
    }
}