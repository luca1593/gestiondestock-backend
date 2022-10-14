package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.FournisseurDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/fournisseur")
public interface FournisseurApi {

    @PostMapping(value = APP_ROOT + "/fournisseur/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'un fournisseur",
            notes = "Cette methode permet d'enregidtre ou de modifier un fournisseur",
            response = FournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet fournisseur creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet fournisseur")
    })
    FournisseurDto save(@RequestBody FournisseurDto dto);

    @GetMapping(value = APP_ROOT + "/fournisseur/{idFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un fournisseur",
            notes = "Cette methode permet de rechercher un fournisseur par son ID",
            response = FournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    FournisseurDto findById(@PathVariable("idFournisseur") Integer id);

    @GetMapping(value = APP_ROOT + "/fournisseur/nom/{nomFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un fournisseur",
            notes = "Cette methode permet de rechercher un fournisseur par son nom",
            response = FournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD le code fournie")
    })
    FournisseurDto findByNomFournisseur(@PathVariable("nomFournisseur") String nom);

    @GetMapping(value = APP_ROOT + "/fournisseur/email/{emailFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un fournisseur",
            notes = "Cette methode permet de rechercher une fournisseur par son email",
            response = FournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet fournisseur n'a pas ete trouver dans la BDD l'email fournie")
    })
    FournisseurDto findByEmailFournisseur(@PathVariable("emailFournisseur") String email);

    @GetMapping(value = APP_ROOT + "/fournisseur/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des fournisseurs",
            notes = "Cette methode permet de rechercher toutes les fournisseurs dans la BDD",
            responseContainer = "List<FournisseurDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des clients / liste vide")
    })
    List<FournisseurDto> findAll();

    @DeleteMapping(value = APP_ROOT +"/fournisseur/detele/{idFournisseur}")
    @ApiOperation(value = "Supprimer un fournisseur",
            notes = "Cette methode permet de supprimer un fournisseur par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le fournisseur a ete supprimer dans la BDD")
    })
    void delete(@PathVariable("idFournisseur") Integer id);

}
