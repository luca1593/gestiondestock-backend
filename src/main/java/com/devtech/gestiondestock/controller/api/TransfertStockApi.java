package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.TransfertStockDto;
import com.devtech.gestiondestock.model.TransfertStock;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface TransfertStockApi {
    @PostMapping(value = APP_ROOT + "/transferts/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    TransfertStockDto save(@RequestBody TransfertStockDto dto);

    @GetMapping(value = APP_ROOT + "/transferts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    TransfertStockDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/transferts/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TransfertStockDto> findAll();

    @GetMapping(value = APP_ROOT + "/transferts/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TransfertStockDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);

    @GetMapping(value = APP_ROOT + "/transferts/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<TransfertStockDto> findByStatut(@PathVariable("statut") TransfertStock.StatutTransfert statut);

    @DeleteMapping(value = APP_ROOT + "/transferts/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
