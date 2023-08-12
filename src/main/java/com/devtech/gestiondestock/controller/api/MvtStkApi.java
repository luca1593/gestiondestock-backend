package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.MvtStkDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

/**
 * @author luca
 */
@CrossOrigin(origins = "*", originPatterns = "*")
public interface MvtStkApi {

    @PostMapping(value = APP_ROOT + "/mvtstk/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'un mouvement de stock", description = "Cette methode permet d'enregidtre ou de modifier un mouvement de stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet mouvement de stock fournisseur creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MvtStkDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet mouvement de stock")
    })
    MvtStkDto save(@RequestBody MvtStkDto dto);

    @GetMapping(value = APP_ROOT + "/mvtstk/{idMvtstk}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un mouvement de stock", description = "Cette methode permet de rechercher un mouvement de stock par son ID")
    @ApiResponses(value = {
            @ApiResponse(description = "200", responseCode = "L'objet commande fournisseur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MvtStkDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet commande fournisseur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    MvtStkDto findById(@PathVariable("idMvtstk") Integer id);

    @GetMapping(value = APP_ROOT + "/mvtstk/date/{dateMvtStk}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une liste mouvements de stock", description = "Cette methode permet de rechercher une liste dd mouvements de stock par date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet mouvements de stock a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MvtStkDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet mouvements de stock n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<MvtStkDto> findMvtStkByDateMvt(@PathVariable("nomClient") Instant dateMvt);

    @GetMapping(value = APP_ROOT + "/mvtstk/type/{typeMvt}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher liste des mouvements de stock par type", description = "Cette methode permet de rechercher liste des mouvements de stock par type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aucun mouvement de stock n'a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MvtStkDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur aucun mouvement de stock n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<MvtStkDto> findMvtStkByType(@PathVariable("typeMvt") String typeMvt);

    @GetMapping(value = APP_ROOT + "/mvtstk/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des mouvements de stock", description = "Cette methode permet de rechercher toutes les mouvements de stock dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des mouvements de stock / liste vide",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MvtStkDto.class)))
            )
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

    @DeleteMapping(value = APP_ROOT + "/mvtstk/detele/{typeMvt}")
    @Operation(summary = "Supprimer une commande client", description = "Cette methode permet de supprimer un mouvement de stock par son ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Le mouvement de stock a ete supprimen dans la BDD")})
    void delete(@PathVariable("typeMvt") Integer id);

}
