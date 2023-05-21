package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.*;

@Api(ENTREPRISE_ENDPOINT)
public interface EntrepriseApi {

    @PostMapping(value = CREATE_ENTREPRISE_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'une entreprise",
            notes = "Cette methode permet d'enregidtre ou de modifier une entreprise",
            response = EntrepriseDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet client creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet client")
    })
    EntrepriseDto save(@RequestBody EntrepriseDto dto);

    @GetMapping(value = FIND_ENTREPRISE_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un entreprise",
            notes = "Cette methode permet de rechercher un entreprise par son ID",
            response = EntrepriseDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet entreprise a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    EntrepriseDto findById(@PathVariable("idEntreprise") Integer id);

    @GetMapping(value = FIND_ENTREPRISE_BY_NOM_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un entreprise",
            notes = "Cette methode permet de rechercher une entreprise par son nom",
            response = EntrepriseDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet entreprise a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD le code fournie")
    })
    EntrepriseDto findByNomEntreprise(@PathVariable("nomEntreprise") String nom);

    @GetMapping(value = FIND_ENTREPRISE_BY_MAIL_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un entreprise",
            notes = "Cette methode permet de rechercher une entreprise par son email",
            response = EntrepriseDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet entreprise a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet entreprise n'a pas ete trouver dans la BDD l'email fournie")
    })
    EntrepriseDto findByEmailEntreprise(@PathVariable("emailEntreprise") String email);

    @GetMapping(value = FIND_ALL_ENTREPRISE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des entreprises",
            notes = "Cette methode permet de rechercher toutes les entreprises dans la BDD",
            responseContainer = "List<EntrepriseDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des clients / liste vide")
    })
    List<EntrepriseDto> findAll();

    @DeleteMapping(value = DELETE_ENTREPRISE_BY_ID_ENDPOINT)
    @ApiOperation(value = "Supprimer un entreprise",
            notes = "Cette methode permet de supprimer une entreprise par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'entreprise a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idEntreprise") Integer id);

}
