package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.EntrepotDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface EntrepotApi {
    @PostMapping(value = APP_ROOT + "/entrepots/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EntrepotDto save(@RequestBody EntrepotDto dto);
    
    @GetMapping(value = APP_ROOT + "/entrepots/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    EntrepotDto findById(@PathVariable("id") Integer id);
    
    @GetMapping(value = APP_ROOT + "/entrepots/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<EntrepotDto> findAll();
    
    @GetMapping(value = APP_ROOT + "/entrepots/entreprise/{entrepriseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<EntrepotDto> findByEntreprise(@PathVariable("entrepriseId") Integer entrepriseId);
    
    @DeleteMapping(value = APP_ROOT + "/entrepots/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}