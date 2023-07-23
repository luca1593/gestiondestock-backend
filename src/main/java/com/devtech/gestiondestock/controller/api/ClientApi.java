package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ClientDto;
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
public interface ClientApi {

    @PostMapping(value = APP_ROOT + "/client/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'un client",
            description = "Cette methode permet d'enregidtre ou de modifier un client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet client creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet client")
    })
    ClientDto save(@RequestBody ClientDto dto);

    @GetMapping(value = APP_ROOT + "/client/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un client",
            description = "Cette methode permet de rechercher un client par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet client a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet client n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    ClientDto findById(@PathVariable("idClient") Integer id);

    @GetMapping(value = APP_ROOT + "/client/nom/{nomClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un client",
            description = "Cette methode permet de rechercher un client par son nom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet client a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet client n'a pas ete trouver dans la BDD le code fournie")
    })
    ClientDto findbyNomClient(@PathVariable("nomClient") String nom);

    @GetMapping(value = APP_ROOT + "/client/email/{emailClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un client",
            description = "Cette methode permet de rechercher un client par son email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet client a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ClientDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet client n'a pas ete trouver dans la BDD l'email fournie")
    })
    ClientDto findbyEmailClient(@PathVariable("emailClient") String email);

    @GetMapping(value = APP_ROOT + "/client/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des clients",
            description = "Cette methode permet de rechercher toutes les clients dans la BDD"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des clients / liste vide",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ClientDto.class)))
            )
    })
    List<ClientDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/client/detele/{idClient}")
    @Operation(summary = "Supprimer un client",
            description = "Cette methode permet de supprimer un client par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le client a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idClient") Integer id);

}
