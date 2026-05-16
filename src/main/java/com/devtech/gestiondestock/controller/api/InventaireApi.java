package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.InventaireDto;
import com.devtech.gestiondestock.model.Inventaire;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface InventaireApi {
    @PostMapping(value = APP_ROOT + "/inventaires/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    InventaireDto save(@RequestBody InventaireDto dto);

    @GetMapping(value = APP_ROOT + "/inventaires/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    InventaireDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/inventaires/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<InventaireDto> findAll();

    @GetMapping(value = APP_ROOT + "/inventaires/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<InventaireDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);

    @GetMapping(value = APP_ROOT + "/inventaires/entrepot/{entrepotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<InventaireDto> findByEntrepot(@PathVariable("entrepotId") Integer entrepotId);

    @GetMapping(value = APP_ROOT + "/inventaires/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<InventaireDto> findByStatut(@PathVariable("statut") Inventaire.Statut statut);

    @DeleteMapping(value = APP_ROOT + "/inventaires/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
