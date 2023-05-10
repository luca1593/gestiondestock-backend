package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/articles")
public interface ArticleApi {
    @PostMapping(value = APP_ROOT + "/articles/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'un article",
            notes = "Cette methode permet d'enregidtre ou de modifier un article",
            response = ArticleDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet article creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet article")
    })
    ArticleDto save(@RequestBody ArticleDto dto);

    @GetMapping(value = APP_ROOT + "/articles/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un article",
            notes = "Cette methode permet de rechercher un article par son ID",
            response = ArticleDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet article a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet article n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    ArticleDto findById(@PathVariable("idArticle") Integer id);

    @GetMapping(value = APP_ROOT + "/articles/code/{codeArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher un article",
            notes = "Cette methode permet de rechercher un article par son code",
            response = ArticleDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet article a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet article n'a pas ete trouver dans la BDD le code fournie")
    })
    ArticleDto findByCodeArticle(@PathVariable("codeArticle") String code);

    @GetMapping(value= APP_ROOT + "/articles/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des articles",
            notes = "Cette methode permet de rechercher toutes les articles dans la BDD",
            responseContainer = "List<ArticleDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des article / liste vide")
    })
    List<ArticleDto> findAll();

    @GetMapping(value= APP_ROOT + "/articles/historiques/vente/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneVenteDto> findHistoriqueVente(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/historiques/commande-client/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeClientDto> findHistoriqueCommandeClient(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/historiques/commande-fournisseur/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/filtre/category/{idCategore}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ArticleDto> findAllByCategorie(@PathVariable("idCategore") Integer idCategorie);

    @DeleteMapping(value = APP_ROOT + "/articles/delete/{idArticle}")
    @ApiOperation(value = "Supprimer un article",
            notes = "Cette methode permet de supprimer un article par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'article a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idArticle") Integer id);
}
