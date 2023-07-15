package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.CategoryDto;
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

public interface CategoryApi {

    @PostMapping(value = APP_ROOT + "/category/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrement d'une categorie",
            description = "Cette methode permet d'enregidtre ou de modifier une categorie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet categorie creer ou modifier",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la creation de l'objet categorie")
    })
    CategoryDto save(@RequestBody CategoryDto dto);

    @GetMapping(value = APP_ROOT + "/category/{idCategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une categorie",
            description = "Cette methode permet de rechercher une categorie par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet categorie a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet categorie n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    CategoryDto findById(@PathVariable("idCategory") Integer id);

    @GetMapping(value = APP_ROOT + "/category/code/{codeCategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une categorie",
            description = "Cette methode permet de rechercher une categorie par son code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'objet categorie a ete trouver dans la BDD",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur l'objet categorie n'a pas ete trouver dans la BDD le code fournie")
    })
    CategoryDto findByCodeCategory(@PathVariable("codeCategory") String code);

    @GetMapping(value = APP_ROOT + "/category/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des categories",
            description = "Cette methode permet de rechercher toutes les categories dans la BDD"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des categories / liste vide",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))
            )
    })
    List<CategoryDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/category/detele/{idCategory}")
    @Operation(summary = "Supprimer un article",
            description = "Cette methode permet de supprimer un categorie par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le categorie a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idCategory") Integer id);
}
