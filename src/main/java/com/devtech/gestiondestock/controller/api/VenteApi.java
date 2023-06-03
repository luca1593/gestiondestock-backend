package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.VenteDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/vente")
public interface VenteApi {

    @PostMapping(value = APP_ROOT + "/vente/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'une vente",
            notes = "Cette methode permet d'enregidtre ou de modifier une vente",
            response = VenteDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet vente creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet vente")
    })
    VenteDto save(@RequestBody VenteDto dto);

    @GetMapping(value = APP_ROOT + "/vente/{idVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une vente",
            notes = "Cette methode permet de rechercher une vente par son ID",
            response = VenteDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet vente a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet vente n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    VenteDto findById(@PathVariable("idVente") Integer id);

    @GetMapping(value = APP_ROOT + "/vente/code/{codeVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une vente",
            notes = "Cette methode permet de rechercher une vente par son code",
            response = VenteDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet vente a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet vente n'a pas ete trouver dans la BDD le code fournie")
    })
    VenteDto findByCodeVente(@PathVariable("codeVente") String code);

    @GetMapping(value = APP_ROOT + "/vente/date/{dateVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher liste des ventes par date",
            notes = "Cette methode permet de rechercher liste des ventes par date",
            responseContainer = "List<VenteDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Aucune vente n'a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur aucune vente n'a pas ete trouver dans la BDD pendant la date fournie")
    })
    List<VenteDto> findByDateVente(@PathVariable("dateVente") Instant dateVente);

    @GetMapping(value = APP_ROOT + "/vente/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des ventes",
            notes = "Cette methode permet de rechercher toutes les ventes dans la BDD",
            responseContainer = "List<VenteDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des ventes / liste vide")
    })
    List<VenteDto> findAll();

    @DeleteMapping(value = APP_ROOT +"/vente/detele/{idVente}")
    @ApiOperation(value = "Supprimer une vente",
            notes = "Cette methode permet de supprimer un article par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La vente a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idVente") Integer id);

    @GetMapping(value = APP_ROOT + "/vente/list/ligne-vente/{idVente}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des ventes",
            notes = "Cette methode permet de rechercher toutes les lignes de vente par l'id de la vente  dans la BDD",
            responseContainer = "List<LigneVenteDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des lignes de vente par l'id de la vente / liste vide")
    })
    List<LigneVenteDto> findAllByVente(@PathVariable("idVente") Integer idVente);
}
