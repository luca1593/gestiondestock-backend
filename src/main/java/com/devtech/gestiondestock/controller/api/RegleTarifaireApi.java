package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.model.RegleTarifaire;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface RegleTarifaireApi {
    @PostMapping(value = APP_ROOT + "/regles-tarifaires/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    RegleTarifaireDto save(@RequestBody RegleTarifaireDto dto);

    @GetMapping(value = APP_ROOT + "/regles-tarifaires/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    RegleTarifaireDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/regles-tarifaires/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<RegleTarifaireDto> findAll();

    @GetMapping(value = APP_ROOT + "/regles-tarifaires/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<RegleTarifaireDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);

    @GetMapping(value = APP_ROOT + "/regles-tarifaires/categorie/{categorieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<RegleTarifaireDto> findByCategorie(@PathVariable("categorieId") Integer categorieId);

    @GetMapping(value = APP_ROOT + "/regles-tarifaires/actifs", produces = MediaType.APPLICATION_JSON_VALUE)
    List<RegleTarifaireDto> findActifs();

    @DeleteMapping(value = APP_ROOT + "/regles-tarifaires/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
