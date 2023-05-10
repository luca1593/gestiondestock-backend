package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.model.EtatCommande;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.*;

@Api(COMMANDE_FOURNISSEUR_ENDPOINT)
public interface CommandeFournisseurApi {
    @PostMapping(value = CREATE_COMMANDE_FOURNISSEUR, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'une commande fournisseur",
            notes = "Cette methode permet d'enregidtre ou de modifier une commande fournisseur",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet commande fournisseur creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet commande fournisseur")
    })
    CommandeFournisseurDto save(
        @RequestBody CommandeFournisseurDto dto, 
        @PathVariable("dateCommandeFournisseur") Long dateCommandeFournisseur
    );

    @GetMapping(value = FIND_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une commande fournisseur",
            notes = "Cette methode permet de rechercher une commande fournisseur par son ID",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet commande fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet commande fournisseur n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    CommandeFournisseurDto findById(@PathVariable("idCommandeFournisseur")  Integer id);

    @GetMapping(value = FIND_COMMANDE_FOURNISSEUR_BY_CODE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une commande fournisseur",
            notes = "Cette methode permet de rechercher une commande fournisseur par son code",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet commande fournisseur a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet commande fournisseur n'a pas ete trouver dans la BDD le code fournie")
    })
    CommandeFournisseurDto findByCodeCommande(@PathVariable("codeCommandeFournisseur") String code);

    @PatchMapping(value = UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT + "/etat/{idCommande}/{etatCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Mise a jour de l'etat d'une commande fournisseur",
            notes = "Cette methode permet de mettre a jour l'etat d'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'etat de la commande fournisseur a ete mise a jour"),
            @ApiResponse(code = 404, message = "Erreur lors de la mise a jour de l'etat de la commande fournisseur")
    })
    CommandeFournisseurDto updateEtatCommande(@PathVariable("idCommande") Integer id,
                                              @PathVariable("etatCommande") EtatCommande etatCommande);

    @PatchMapping(value = UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT + "/quantite/{idCommande}/{idLigneCommande}/{quantite}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Mise a jour de la quantite d'une commande fournisseur",
            notes = "Cette methode permet de mettre a jour la quantite d'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La quantite de la commande fournisseur a ete mise a jour"),
            @ApiResponse(code = 404, message = "Erreur lors de la mise a jour de la quantite de la commande fournisseur")
    })
    CommandeFournisseurDto updateQuantiterCommande(@PathVariable("idCommande") Integer idCommande,
                                                   @PathVariable("idLigneCommande") Integer idLigneCommande,
                                                   @PathVariable("quantite") BigDecimal quantite);

    @PatchMapping(value = UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT + "/fournisseur/{idCommande}/{idFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Mise a jour du fournisseur d'une commande fournisseur",
            notes = "Cette methode permet de mettre a jour le fournisseur d'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le fournisseur de la commande fournisseur a ete mise a jour"),
            @ApiResponse(code = 404, message = "Erreur lors de la mise a jour du fournisseur de la commande fournisseur")
    })
    CommandeFournisseurDto updateFournisseur(@PathVariable("idCommande") Integer idCommande,
                                             @PathVariable("idFournisseur") Integer idFournisseur);

    @PatchMapping(value = UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT + "/article/{idCommande}/{idLigneCommande}/{newIdArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Mise a jour de l'article d'une commande fournisseur",
            notes = "Cette methode permet de mettre a jour l'article d'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'article de la commande fournisseur a ete mise a jour"),
            @ApiResponse(code = 404, message = "Erreur lors de la mise a jour de l'article de la commande fournisseur")
    })
    CommandeFournisseurDto updateArticle(@PathVariable("idCommande") Integer idCommande,
                                         @PathVariable("idLigneCommande") Integer idLigneCommande,
                                         @PathVariable("newIdArticle") Integer newIdArticle);

    @DeleteMapping(value = COMMANDE_FOURNISSEUR_ENDPOINT + "/delete/article/{idCommande}/{idLigneCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Suppression d'un articl d'une commande fournisseur",
            notes = "Cette methode permet de supprimer un article sur 'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'article de la commande fournisseur a ete supprimer avec success"),
            @ApiResponse(code = 404, message = "Erreur lors de la suppression de la commande fournisseur")
    })
    CommandeFournisseurDto deleteArticle(@PathVariable("idCommande") Integer idCommande,
                                         @PathVariable("idLigneCommande") Integer idLigneCommande);

    @GetMapping(value = COMMANDE_FOURNISSEUR_ENDPOINT + "/list/ligne-commande/{idCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Recherche de la liste des lignes de commande d'une commande fournisseur",
            notes = "Cette methode permet de rechercher la liste des lignes de commande d'une commande fournisseur par son id",
            response = CommandeFournisseurDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Liste des lignes de commande fournisseur a ete trouver avec succesr"),
            @ApiResponse(code = 404, message = "Erreur lors de la recherche de la liste des lignes de commande fournisseur")
    })
    List<LigneCommandeFournisseurDto> findAllByCommandeFournisseur(@PathVariable("idCommande") Integer idCommande);

    @GetMapping(value= FIND_COMMANDE_FOURNISSEUR_BY_DATE_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher liste des commandes fournisseur par date",
            notes = "Cette methode permet de rechercher liste des commandes fournisseur par date",
            responseContainer = "List<CommandeFournisseurDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Aucune commande fournisseur n'a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur aucune commande fournisseur n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<CommandeFournisseurDto> findByDateCommande(@PathVariable("dateCommandeFournisseur") Instant dateCommande);

    @GetMapping(value = FIND_ALL_COMMANDE_FOURNISSEUR)
    @ApiOperation(value = "Renvoie la liste des commandes fournisseur",
            notes = "Cette methode permet de rechercher toutes les commandes fournisseur dans la BDD",
            responseContainer = "List<CommandeFournisseurDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des commandes fournisseur / liste vide")
    })
    List<CommandeFournisseurDto> findAll();

    @PostMapping(value = COMMANDE_FOURNISSEUR_ENDPOINT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des commandes fournisseur selon le fournisseur",
            notes = "Cette methode permet de rechercher toutes les commandes fournisseur selon le fournisseur",
            responseContainer = "List<CommandeFournisseurDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des commandes fournisseur selon le fournisseur / liste vide")
    })
    List<CommandeFournisseurDto> findAllByFournisseur(@RequestBody FournisseurDto dto);

    @DeleteMapping(value = DELETE_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT)
    @ApiOperation(value = "Supprimer une commande client",
            notes = "Cette methode permet de supprimer une commande fournisseur par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La commande fournisseur a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idCommandeFournisseur") Integer id);
}
