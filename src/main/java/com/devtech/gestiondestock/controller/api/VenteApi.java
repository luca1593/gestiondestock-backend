package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.VenteDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

/**
 * @author luca
 */
@CrossOrigin(origins = "*", originPatterns = "*")
public interface VenteApi {

    @PostMapping(value = APP_ROOT + "/vente/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'une vente", description = "Cette methode permet d'enregidtre ou de modifier une vente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet vente creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VenteDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet vente")
    })
    VenteDto save(@RequestBody VenteDto dto);

    @GetMapping(value = APP_ROOT + "/vente/{idVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une vente", description = "Cette methode permet de rechercher une vente par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet vente a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VenteDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet vente n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    VenteDto findById(@PathVariable("idVente") Integer id);

    @GetMapping(value = APP_ROOT + "/vente/code/{codeVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une vente", description = "Cette methode permet de rechercher une vente par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet vente a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VenteDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet vente n'a pas ete trouver dans la BDD le code fournie")
    })
    VenteDto findByCodeVente(@PathVariable("codeVente") String code);

    @GetMapping(value = APP_ROOT + "/vente/date/{dateVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher liste des ventes par date", description = "Cette methode permet de rechercher liste des ventes par date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aucune vente n'a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VenteDto.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur aucune vente n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<VenteDto> findByDateVente(@PathVariable("dateVente") Instant dateVente);

    @GetMapping(value = APP_ROOT + "/vente/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des ventes", description = "Cette methode permet de rechercher toutes les ventes dans la BDD")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "La liste des ventes / liste vide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VenteDto.class))))})
    List<VenteDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/vente/detele/{idVente}")
    @Operation(summary = "Supprimer une vente", description = "Cette methode permet de supprimer un article par son ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "La vente a ete supprimen dans la BDD")})
    void delete(@PathVariable("idVente") Integer id);

    @GetMapping(value = APP_ROOT + "/vente/list/ligne-vente/{idVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des ventes", description = "Cette methode permet de rechercher toutes les lignes de vente par l'id de la vente  dans la BDD")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "La liste des lignes de vente par l'id de la vente / liste vide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = LigneVenteDto.class)))
    )})
    List<LigneVenteDto> findAllByVente(@PathVariable("idVente") Integer idVente);
}
