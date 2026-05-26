package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.model.Paiement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface PaiementApi {
    @PostMapping(value = APP_ROOT + "/paiements/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    PaiementDto save(@RequestBody PaiementDto dto);

    @GetMapping(value = APP_ROOT + "/paiements/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    PaiementDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/paiements/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<PaiementDto> findAll();

    @GetMapping(value = APP_ROOT + "/paiements/facture/{factureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<PaiementDto> findByFacture(@PathVariable("factureId") Integer factureId);

    @DeleteMapping(value = APP_ROOT + "/paiements/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
