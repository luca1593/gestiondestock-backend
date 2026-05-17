package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface FactureApi {
    @PostMapping(value = APP_ROOT + "/factures/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    FactureDto save(@RequestBody FactureDto dto);

    @GetMapping(value = APP_ROOT + "/factures/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    FactureDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/factures/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findAll();

    @GetMapping(value = APP_ROOT + "/factures/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByClient(@PathVariable("clientId") Integer clientId);

    @GetMapping(value = APP_ROOT + "/factures/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);

    @GetMapping(value = APP_ROOT + "/factures/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByStatut(@PathVariable("statut") Facture.StatutFacture statut);

    @DeleteMapping(value = APP_ROOT + "/factures/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
