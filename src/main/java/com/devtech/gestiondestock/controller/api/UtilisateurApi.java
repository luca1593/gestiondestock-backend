package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ChangerMotDePasseUtilisateurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
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
public interface UtilisateurApi {
    @PostMapping(value = APP_ROOT + "/utilisateur/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'un utilisateur", description = "Cette methode permet d'enregidtre ou de modifier un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet utilisateur creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UtilisateurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet utilisateur")
    })
    UtilisateurDto save(@RequestBody UtilisateurDto dto);

    @GetMapping(value = APP_ROOT + "/utilisateur/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur", description = "Cette methode permet de rechercher un utilisateur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet utilisateur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UtilisateurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    UtilisateurDto findById(@PathVariable("idUtilisateur") Integer id);

    @GetMapping(value = APP_ROOT + "/utilisateur/nom/{nomUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur", description = "Cette methode permet de rechercher un utilisateur par son nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet utilisateur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UtilisateurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD le code fournie")
    })
    UtilisateurDto findByNomUtilisateur(@PathVariable("nomUtilisateur") String nom);

    @GetMapping(value = APP_ROOT + "/utilisateur/email/{emailUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur", description = "Cette methode permet de rechercher une utilisateur par son email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet utilisateur a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UtilisateurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet utilisateur n'a pas ete trouver dans la BDD l'email fournie")
    })
    UtilisateurDto findByEmailUtilisateur(@PathVariable("emailUtilisateur") String email);

    @GetMapping(value = APP_ROOT + "/utilisateur/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des utilisateurs", description = "Cette methode permet de rechercher toutes les utilisateurs dans la BDD")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "La liste des clients / liste vide",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UtilisateurDto.class))))})
    List<UtilisateurDto> findAll();

    @PostMapping(value = APP_ROOT + "/utilisateur/changer-mot-de-passe", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie un profil utilisateur", description = "Cette methode permet de changer le mot de passe de l'utilisateurs dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe changer avec succes",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UtilisateurDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur : Le mot de passe ne peut pas etre modifier")
    })
    UtilisateurDto changerMotDePasse(@RequestBody ChangerMotDePasseUtilisateurDto dto);

    @DeleteMapping(value = APP_ROOT + "/utilisateur/detele/{idUtilisateur}")
    @Operation(summary = "Supprimer un utilisateur", description = "Cette methode permet de supprimer un utilisateur par son ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Utilisateur supprimer avec success")})
    void delete(@PathVariable("idUtilisateur") Integer id);
}
