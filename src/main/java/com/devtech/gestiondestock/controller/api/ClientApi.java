package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ClientDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/client")
public interface ClientApi {

    @PostMapping(value = APP_ROOT + "/client/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'un client",
            notes = "Cette methode permet d'enregidtre ou de modifier un client",
            response = ClientDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet client creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet client")
    })
    ClientDto save(@RequestBody ClientDto dto);

    @GetMapping(value = APP_ROOT + "/client/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un client",
            notes = "Cette methode permet de rechercher un client par son ID",
            response = ClientDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet client a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet client n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    ClientDto findById(@PathVariable("idClient") Integer id);

    @GetMapping(value = APP_ROOT + "/client/nom/{nomClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un client",
            notes = "Cette methode permet de rechercher un client par son nom",
            response = ClientDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet client a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet client n'a pas ete trouver dans la BDD le code fournie")
    })
    ClientDto findbyNomClient(@PathVariable("nomClient") String nom);

    @GetMapping(value = APP_ROOT + "/client/email/{emailClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un client",
            notes = "Cette methode permet de rechercher un client par son email",
            response = ClientDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet client a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet client n'a pas ete trouver dans la BDD l'email fournie")
    })
    ClientDto findbyEmailClient(@PathVariable("emailClient") String email);

    @GetMapping(value = APP_ROOT + "/client/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des clients",
            notes = "Cette methode permet de rechercher toutes les clients dans la BDD",
            responseContainer = "List<ClientDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des clients / liste vide")
    })
    List<ClientDto> findAll();

    @DeleteMapping(value = APP_ROOT +"/client/detele/{idClient}")
    @ApiOperation(value = "Supprimer un client",
            notes = "Cette methode permet de supprimer un client par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le client a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idClient") Integer id);

}
