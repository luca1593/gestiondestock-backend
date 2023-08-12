package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
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
public interface ArticleApi {
    @PostMapping(value = APP_ROOT + "/articles/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'un article",
            description = "Cette methode permet d'enregidtre ou de modifier un article"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet article creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ArticleDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet article")
    })
    ArticleDto save(@RequestBody ArticleDto dto);

    @GetMapping(value = APP_ROOT + "/articles/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un article",
            description = "Cette methode permet de rechercher un article par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet article a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ArticleDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet article n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    ArticleDto findById(@PathVariable("idArticle") Integer id);

    @GetMapping(value = APP_ROOT + "/articles/code/{codeArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un article",
            description = "Cette methode permet de rechercher un article par son code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet article a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ArticleDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet article n'a pas ete trouver dans la BDD le code fournie")
    })
    ArticleDto findByCodeArticle(@PathVariable("codeArticle") String code);

    @GetMapping(value = APP_ROOT + "/articles/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des articles",
            description = "Cette methode permet de rechercher toutes les articles dans la BDD"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "La liste des article / liste vide",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ArticleDto.class)))
            )})
    List<ArticleDto> findAll();

    @GetMapping(value = APP_ROOT + "/articles/historiques/vente/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneVenteDto> findHistoriqueVente(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/historiques/commande-client/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeClientDto> findHistoriqueCommandeClient(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/historiques/commande-fournisseur/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(@PathVariable("idArticle") Integer idArticle);
    @GetMapping(value= APP_ROOT + "/articles/filtre/category/{idCategore}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ArticleDto> findAllByCategorie(@PathVariable("idCategore") Integer idCategorie);

    @DeleteMapping(value = APP_ROOT + "/articles/delete/{idArticle}")
    @Operation(summary = "Supprimer un article", description = "Cette methode permet de supprimer un article par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'article a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idArticle") Integer id);
}
