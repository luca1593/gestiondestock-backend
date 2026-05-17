package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.LotDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

public interface LotApi {
    @PostMapping(value = APP_ROOT + "/lots/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    LotDto save(@RequestBody LotDto dto);

    @GetMapping(value = APP_ROOT + "/lots/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    LotDto findById(@PathVariable("id") Integer id);

    @GetMapping(value = APP_ROOT + "/lots/all", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LotDto> findAll();

    @GetMapping(value = APP_ROOT + "/lots/article/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LotDto> findByArticle(@PathVariable("articleId") Integer articleId);

    @GetMapping(value = APP_ROOT + "/lots/entrepot/{entrepotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LotDto> findByEntrepot(@PathVariable("entrepotId") Integer entrepotId);

    @GetMapping(value = APP_ROOT + "/lots/available", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LotDto> findAvailable();

    @DeleteMapping(value = APP_ROOT + "/lots/delete/{id}")
    void delete(@PathVariable("id") Integer id);
}
