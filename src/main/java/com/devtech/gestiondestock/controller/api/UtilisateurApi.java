package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/utilisateur")
public interface UtilisateurApi {
    @PostMapping(value = APP_ROOT + "/utilisateur/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'un utilisateur",
            notes = "Cette methode permet d'enregidtre ou de modifier un utilisateur",
            response = UtilisateurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet utilisateur creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet utilisateur")
    })
    UtilisateurDto save(@RequestBody UtilisateurDto dto);
    @GetMapping(value = APP_ROOT + "/utilisateur/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un utilisateur",
            notes = "Cette methode permet de rechercher un utilisateur par son ID",
            response = UtilisateurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet utilisateur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    UtilisateurDto findById(@PathVariable("idUtilisateur") Integer id);
    @GetMapping(value = APP_ROOT + "/utilisateur/nom/{nomUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un utilisateur",
            notes = "Cette methode permet de rechercher un utilisateur par son nom",
            response = UtilisateurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet utilisateur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD le code fournie")
    })
    UtilisateurDto findByNomUtilisateur(@PathVariable("nomUtilisateur") String nom);
    @GetMapping(value = APP_ROOT + "/utilisateur/email/{emailUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un utilisateur",
            notes = "Cette methode permet de rechercher une utilisateur par son email",
            response = UtilisateurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet utilisateur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD l'email fournie")
    })
    UtilisateurDto findByEmailUtilisateur(@PathVariable("emailUtilisateur") String email);
    @GetMapping(value = APP_ROOT + "/utilisateur/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des utilisateurs",
            notes = "Cette methode permet de rechercher toutes les utilisateurs dans la BDD",
            responseContainer = "List<UtilisateurDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des clients / liste vide")
    })
    List<UtilisateurDto> findAll();

    @PostMapping(value = APP_ROOT + "/utilisateur/changer-mot-de-passe", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie un profil utilisateur",
            notes = "Cette methode permet de changer le mot de passe de l'utilisateurs dans la BDD",
            response = UtilisateurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Mot de passe changer avec succes"),
            @ApiResponse(code = 404, message = "Erreur : Le mot de passe ne peut pas etre modifier")
    })
    UtilisateurDto changerMotDePasse(@RequestBody ChangerMotDePasseUtilisateurDto dto);

    @DeleteMapping(value = APP_ROOT +"/utilisateur/detele/{idUtilisateur}")
    @ApiOperation(value = "Supprimer un utilisateur",
            notes = "Cette methode permet de supprimer un utilisateur par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Utilisateur supprimer avec success")
    })
    void delete(@PathVariable("idUtilisateur") Integer id);
}
