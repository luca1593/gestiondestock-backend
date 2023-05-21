package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.MvtStkDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/mvtstk")
public interface MvtStkApi {

    @PostMapping(value = APP_ROOT + "/mvtstk/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'un mouvement de stock",
            notes = "Cette methode permet d'enregidtre ou de modifier un mouvement de stock",
            response = MvtStkDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet mouvement de stock fournisseur creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet mouvement de stock")
    })
    MvtStkDto save(@RequestBody MvtStkDto dto);

    @GetMapping(value = APP_ROOT + "/mvtstk/{idMvtstk}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un mouvement de stock",
            notes = "Cette methode permet de rechercher un mouvement de stock par son ID",
            response = MvtStkDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet commande fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet commande fournisseur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    MvtStkDto findById(@PathVariable("idMvtstk") Integer id);

    @GetMapping(value = APP_ROOT + "/mvtstk/date/{dateMvtStk}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une liste mouvements de stock",
            notes = "Cette methode permet de rechercher une liste dd mouvements de stock par date",
            responseContainer = "List<MvtStkDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet mouvements de stock a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet mouvements de stock n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<MvtStkDto> findMvtStkByDateMvt(@PathVariable("nomClient") Instant dateMvt);

    @GetMapping(value = APP_ROOT + "/mvtstk/type/{typeMvt}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher liste des mouvements de stock par type",
            notes = "Cette methode permet de rechercher liste des mouvements de stock par type",
            responseContainer = "List<MvtStkDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Aucun mouvement de stock n'a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur aucun mouvement de stock n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<MvtStkDto> findMvtStkByType(@PathVariable("typeMvt") String typeMvt);

    @GetMapping(value = APP_ROOT + "/mvtstk/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des mouvements de stock",
            notes = "Cette methode permet de rechercher toutes les mouvements de stock dans la BDD",
            responseContainer = "List<MvtStkDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des mouvements de stock / liste vide")
    })
    List<MvtStkDto> findAll();

    @GetMapping(value = APP_ROOT + "/mvtstk/stockreel/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    BigDecimal stockReelArticle(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value = APP_ROOT + "/mvtstk/filter/article/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<MvtStkDto> mvtStkArticle(@PathVariable("idArticle") Integer idArticle);
    @PostMapping(value = APP_ROOT + "/mvtstk/entre", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MvtStkDto entreMvtStk(@RequestBody MvtStkDto dto);
    @PostMapping(value = APP_ROOT + "/mvtstk/sortie", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MvtStkDto sortieMvtStk(@RequestBody MvtStkDto dto);
    @PostMapping(value = APP_ROOT + "/mvtstk/correction-pos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MvtStkDto correctionMvtStkPos(@RequestBody MvtStkDto dto);
    @PostMapping(value = APP_ROOT + "/mvtstk/correction-neg", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    MvtStkDto correctionMvtStkNeg(@RequestBody MvtStkDto dto);

    @DeleteMapping(value = APP_ROOT +"/mvtstk/detele/{typeMvt}")
    @ApiOperation(value = "Supprimer une commande client",
            notes = "Cette methode permet de supprimer un mouvement de stock par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le mouvement de stock a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("typeMvt") Integer id);

}
