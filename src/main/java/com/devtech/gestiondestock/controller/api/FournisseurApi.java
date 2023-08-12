package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.FournisseurDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

/**
 * @author luca
 */
@CrossOrigin(origins = "*", originPatterns = "*")
public interface FournisseurApi {

    @PostMapping(value = APP_ROOT + "/fournisseur/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'un fournisseur", description = "Cette methode permet d'enregidtre ou de modifier un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet fournisseur creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FournisseurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet fournisseur")
    })
    FournisseurDto save(@RequestBody FournisseurDto dto);

    @GetMapping(value = APP_ROOT + "/fournisseur/{idFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un fournisseur", description = "Cette methode permet de rechercher un fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet fournisseur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FournisseurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    FournisseurDto findById(@PathVariable("idFournisseur") Integer id);

    @GetMapping(value = APP_ROOT + "/fournisseur/nom/{nomFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un fournisseur", description = "Cette methode permet de rechercher un fournisseur par son nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet fournisseur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FournisseurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD le code fournie")
    })
    FournisseurDto findByNomFournisseur(@PathVariable("nomFournisseur") String nom);

    @GetMapping(value = APP_ROOT + "/fournisseur/email/{emailFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un fournisseur", description = "Cette methode permet de rechercher une fournisseur par son email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet fournisseur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FournisseurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD l'email fournie")
    })
    FournisseurDto findByEmailFournisseur(@PathVariable("emailFournisseur") String email);

    @GetMapping(value = APP_ROOT + "/fournisseur/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des fournisseurs", description = "Cette methode permet de rechercher toutes les fournisseurs dans la BDD")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "La liste des clients / liste vide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = FournisseurDto.class)))
    )})
    List<FournisseurDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/fournisseur/detele/{idFournisseur}")
    @Operation(summary = "Supprimer un fournisseur", description = "Cette methode permet de supprimer un fournisseur par son ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Le fournisseur a ete supprimer dans la BDD")})
    void delete(@PathVariable("idFournisseur") Integer id);

}
