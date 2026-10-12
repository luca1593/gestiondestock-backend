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

    @PostMapping(value = APP_ROOT + "/factures/save-avec-paiement", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    FactureDto saveAvecPaiement(@RequestBody FactureDto dto);

    @GetMapping(value = APP_ROOT + "/factures/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    FactureDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/factures/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findAll();

    @GetMapping(value = APP_ROOT + "/factures/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByClient(@PathVariable("clientId") Integer clientId);

    @GetMapping(value = APP_ROOT + "/factures/fournisseur/{fournisseurId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByFournisseur(@PathVariable("fournisseurId") Integer fournisseurId);

    @GetMapping(value = APP_ROOT + "/factures/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);

    @GetMapping(value = APP_ROOT + "/factures/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByStatut(@PathVariable("statut") Facture.StatutFacture statut);

    @GetMapping(value = APP_ROOT + "/factures/type/{typeFacture}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findByType(@PathVariable("typeFacture") Facture.TypeFacture typeFacture);

    @GetMapping(value = APP_ROOT + "/factures/source/{sourceType}/{sourceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<FactureDto> findBySource(@PathVariable("sourceType") Facture.SourceDocument sourceType,
                                  @PathVariable("sourceId") Integer sourceId);

    @DeleteMapping(value = APP_ROOT + "/factures/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
