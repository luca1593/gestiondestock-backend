package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.VenteApi;
import com.devtech.gestiondestock.dto.VenteDto;
import com.devtech.gestiondestock.services.VenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
public class VenteController implements VenteApi {

    private VenteService venteService;

    @Autowired
    public VenteController(VenteService venteService){
        this.venteService = venteService;
    }

    @Override
    public VenteDto save(VenteDto dto) {
        return venteService.save(dto);
    }

    @Override
    public VenteDto findById(Integer id) {
        return venteService.findById(id);
    }

    @Override
    public VenteDto findByCodeVente(String code) {
        return venteService.findByCodeVente(code);
    }

    @Override
    public List<VenteDto> findByDateVente(Instant dateVente) {
        return venteService.findByDateVente(dateVente);
    }

    @Override
    public List<VenteDto> findAll() {
        return venteService.findAll();
    }

    @Override
    public void delete(Integer id) {
        venteService.delete(id);
    }
}
