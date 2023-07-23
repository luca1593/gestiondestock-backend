package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.VenteApi;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.VenteDto;
import com.devtech.gestiondestock.services.VenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * @author luca
 */
@RestController
public class VenteController implements VenteApi {

    private VenteService venteService;

    @Autowired
    public VenteController(VenteService venteService){
        this.venteService = venteService;
    }

    @Override
    public VenteDto save(VenteDto dto) {
        return this.venteService.save(dto);
    }

    @Override
    public VenteDto findById(Integer id) {
        return this.venteService.findById(id);
    }

    @Override
    public VenteDto findByCodeVente(String code) {
        return this.venteService.findByCodeVente(code);
    }

    @Override
    public List<VenteDto> findByDateVente(Instant dateVente) {
        return this.venteService.findByDateVente(dateVente);
    }

    @Override
    public List<VenteDto> findAll() {
        return this.venteService.findAll();
    }

    @Override
    public void delete(Integer id) {
        this.venteService.delete(id);
    }

    @Override
    public List<LigneVenteDto> findAllByVente(Integer idVente) {
        return this.venteService.findAllLigneVenteByVente(idVente);
    }
}
