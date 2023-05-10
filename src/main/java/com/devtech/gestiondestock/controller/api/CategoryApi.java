package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.CategoryDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;
@Api(APP_ROOT + "/category")
public interface CategoryApi {

    @PostMapping(value = APP_ROOT + "/category/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Enregistrement d'une categorie",
            notes = "Cette methode permet d'enregidtre ou de modifier une categorie",
            response = CategoryDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet categorie creer ou modifier"),
            @ApiResponse(code = 404, message = "Erreur dans la creation de l'objet categorie")
    })
    CategoryDto save(@RequestBody CategoryDto dto);

    @GetMapping(value = APP_ROOT + "/category/{idCategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une categorie",
            notes = "Cette methode permet de rechercher une categorie par son ID",
            response = CategoryDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet categorie a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet categorie n'a pas ete trouver dans la BDD avec l'ID fournie")
    })
    CategoryDto findById(@PathVariable("idCategory") Integer id);

    @GetMapping(value = APP_ROOT + "/category/code/{codeCategory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Rechercher une categorie",
            notes = "Cette methode permet de rechercher une categorie par son code",
            response = CategoryDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'objet categorie a ete trouver dans la BDD"),
            @ApiResponse(code = 404, message = "Erreur l'objet categorie n'a pas ete trouver dans la BDD le code fournie")
    })
    CategoryDto findByCodeCategory(@PathVariable("codeCategory") String code);

    @GetMapping(value = APP_ROOT + "/category/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Renvoie la liste des categories",
            notes = "Cette methode permet de rechercher toutes les categories dans la BDD",
            responseContainer = "List<CategoryDto>"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "La liste des categories / liste vide")
    })
    List<CategoryDto> findAll();

    @DeleteMapping(value = APP_ROOT +"/category/detele/{idCategory}")
    @ApiOperation(value = "Supprimer un article",
            notes = "Cette methode permet de supprimer un categorie par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le categorie a ete supprimen dans la BDD")
    })
    void delete(@PathVariable("idCategory") Integer id);
}
