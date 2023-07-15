package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.*;


/**
 * @author luca
 */
public interface EntrepriseApi {

    @PostMapping(value = CREATE_ENTREPRISE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'une entreprise", description = "Cette methode permet d'enregidtre ou de modifier une entreprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet client creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntrepriseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet client")
    })
    EntrepriseDto save(@RequestBody EntrepriseDto dto);

    @GetMapping(value = FIND_ENTREPRISE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un entreprise", description = "Cette methode permet de rechercher un entreprise par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet entreprise a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntrepriseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    EntrepriseDto findById(@PathVariable("idEntreprise") Integer id);

    @GetMapping(value = FIND_ENTREPRISE_BY_NOM_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un entreprise", description = "Cette methode permet de rechercher une entreprise par son nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet entreprise a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntrepriseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD le code fournie")
    })
    EntrepriseDto findByNomEntreprise(@PathVariable("nomEntreprise") String nom);

    @GetMapping(value = FIND_ENTREPRISE_BY_MAIL_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un entreprise", description = "Cette methode permet de rechercher une entreprise par son email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet entreprise a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntrepriseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD l'email fournie")
    })
    EntrepriseDto findByEmailEntreprise(@PathVariable("emailEntreprise") String email);

    @GetMapping(value = FIND_ALL_ENTREPRISE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des entreprises", description = "Cette methode permet de rechercher toutes les entreprises dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des clients / liste vide",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = EntrepriseDto.class)))
            )
    })
    List<EntrepriseDto> findAll();

    @DeleteMapping(value = DELETE_ENTREPRISE_BY_ID_ENDPOINT)
    @Operation(summary = "Supprimer un entreprise", description = "Cette methode permet de supprimer une entreprise par son ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "L'entreprise a ete supprimen dans la BDD")})
    void delete(@PathVariable("idEntreprise") Integer id);

}
