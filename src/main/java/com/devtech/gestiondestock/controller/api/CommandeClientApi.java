package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.model.EtatCommande;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

//@Api(APP_ROOT + "/commande-client")
public interface CommandeClientApi {
        @PostMapping(value = APP_ROOT
                + "/commande-client/create/{dateCommandeClient}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Enregistrement d'une commande client", description = "Cette methode permet d'enregidtre ou de modifier une commande client")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'objet commande client creer ou modifier",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommandeClientDto.class))
                ),
                @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet commande client")
        })
        ResponseEntity<CommandeClientDto> save(
                        @RequestBody CommandeClientDto dto,
                        @PathVariable("dateCommandeClient") Long dateCommandeClient);

        @PatchMapping(value = APP_ROOT
                        + "/commande-client/update/etat/{idCommande}/{etatCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'etat de la commande client a bien ete modifier"),
                @ApiResponse(responseCode = "404", description = "Erreur l'etat commande client ne peut pas etre modifier")
        })
        ResponseEntity<CommandeClientDto> updateEtatCommande(@PathVariable("idCommande") Integer id,
                        @PathVariable("etatCommande") EtatCommande etatCommande);

        @PatchMapping(value = APP_ROOT
                        + "/commande-client/update/quantite/{idCommande}/{idLigneCommande}/{quantite}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "La quantite de la commande client a bien ete modifier"),
                @ApiResponse(responseCode = "404", description = "Erreur l'etat commande client ne peut pas etre modifier")
        })
        ResponseEntity<CommandeClientDto> updateQuantiterCommande(@PathVariable("idCommande") Integer idCommande,
                        @PathVariable("idLigneCommande") Integer idLigneCommande,
                        @PathVariable("quantite") BigDecimal quantite);

        @PatchMapping(value = APP_ROOT
                        + "/commande-client/update/client/{idCommande}/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Le client de la commande client a bien ete modifier"),
                @ApiResponse(responseCode = "404", description = "Erreur l'etat commande client ne peut pas etre modifier")
        })
        ResponseEntity<CommandeClientDto> updateClient(@PathVariable("idCommande") Integer idCommande,
                        @PathVariable("idClient") Integer idClient);

        @PatchMapping(value = APP_ROOT
                        + "/commande-client/update/article/{idCommande}/{idLigneCommande}/{newIdArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'article de la commande client a bien ete modifier"),
                @ApiResponse(responseCode = "404", description = "Erreur l'etat commande client ne peut pas etre modifier")
        })
        ResponseEntity<CommandeClientDto> updateArticle(@PathVariable("idCommande") Integer idCommande,
                        @PathVariable("idLigneCommande") Integer idLigneCommande,
                        @PathVariable("newIdArticle") Integer newIdArticle);

        @DeleteMapping(value = APP_ROOT
                        + "/commande-client/delete/article/{idCommande}/{idLigneCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'article de la commande client a bien ete supprimer"),
                @ApiResponse(responseCode = "404", description = "Erreur l'etat commande client ne peut pas etre modifier")
        })
        ResponseEntity<CommandeClientDto> deleteArticle(@PathVariable("idCommande") Integer idCommande,
                        @PathVariable("idLigneCommande") Integer idLigneCommande);

        @GetMapping(value = APP_ROOT
                        + "/commande-client/list/linge-commande/{idCommande}", produces = MediaType.APPLICATION_JSON_VALUE)
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Les lignes de commante de la commande client ont bien ete trouver"),
                @ApiResponse(responseCode = "404", description = "Erreur les lignes de commande client n'ont pas ete trouver")
        })
        ResponseEntity<List<LigneCommandeClientDto>> findAllByCommandeClient(
                        @PathVariable("idCommande") Integer idCommande);

        @GetMapping(value = APP_ROOT
                + "/commande-client/{idCommandeClient}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Rechercher une commande client", description = "Cette methode permet de rechercher une commande client par son ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'objet commande client a ete trouver dans la BDD",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommandeClientDto.class))
                ),
                @ApiResponse(responseCode = "404", description = "Erreur l'objet commande client n'a pas ete trouver dans la BDD avec l'ID fournie")
        })
        ResponseEntity<CommandeClientDto> findById(@PathVariable("idCommandeClient") Integer id);

        @GetMapping(value = APP_ROOT
                + "/commande-client/code/{codeCommandeClient}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Rechercher une commande client", description = "Cette methode permet de rechercher une commande client par son code")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "L'objet commande client a ete trouver dans la BDD",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommandeClientDto.class))
                ),
                @ApiResponse(responseCode = "404", description = "Erreur l'objet commande client n'a pas ete trouver dans la BDD le code fournie")
        })
        ResponseEntity<CommandeClientDto> findByCodeCommande(@PathVariable("codeCommandeClient") String code);

        @GetMapping(value = APP_ROOT
                + "/commande-client/date/{dateCommandeClient}", produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Rechercher liste des commandes client par date",
                description = "Cette methode permet de rechercher liste des commandes client par date")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Aucune commande client n'a ete trouver dans la BDD",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CommandeClientDto.class)))
                ),
                @ApiResponse(responseCode = "404", description = "Erreur aucune commande client n'a pas ete trouver dans la BDD pendant la date fournie")
        })
        ResponseEntity<List<CommandeClientDto>> findByDateCommande(
                        @PathVariable("dateCommandeClient") Instant dateCommande);

        @GetMapping(value = APP_ROOT + "/commande-client/all")
        @Operation(summary = "Renvoie la liste des commandes client",
                description = "Cette methode permet de rechercher toutes les commandes client dans la BDD")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "La liste des commandes client / liste vide",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CommandeClientDto.class)))
                )
        })
        ResponseEntity<List<CommandeClientDto>> findAll();

        @PostMapping(value = APP_ROOT + "/commande-client", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @Operation(summary = "Renvoie la liste des commandes client selon le client",
                description = "Cette methode permet de rechercher toutes les commandes client selon le client")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "La liste des commandes client selon le client / liste vide",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CommandeClientDto.class)))
                )
        })
        ResponseEntity<List<CommandeClientDto>> findAllByClient(@RequestBody ClientDto dto);

        @DeleteMapping(value = APP_ROOT + "/commande-client/delete/{idCommandeClient}")
        @Operation(summary = "Supprimer une commande client", description = "Cette methode permet de supprimer une commande client par son ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "La commande client a ete supprimen dans la BDD")
        })
        ResponseEntity delete(@PathVariable("idCommandeClient") Integer id);

        @GetMapping(value = APP_ROOT + "/commande-client/list/ligne-commande/{idCommande}")
        @Operation(summary = "Renvoie la liste des lignes de commande client", description = "Cette methode permet de rechercher la liste des lignes de commande client par l'id de la commande")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "La liste des commandes client / liste vide",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = LigneCommandeClientDto.class)))
                )
        })
        ResponseEntity<List<LigneCommandeClientDto>> findAllLigneCommandeClient(@PathVariable("idCommande") Integer icCommade);
}
