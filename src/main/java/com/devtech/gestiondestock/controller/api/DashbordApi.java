package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;
import com.devtech.gestiondestock.dto.dasboard.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

/**
 * @author luca
 */
public interface DashbordApi {
    @GetMapping(value = APP_ROOT + "/dashbord/story", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Recupere l'historique des données",
            description = "Cette methode permet de recuperer l'historique les données globale"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List des données selon l'intérvale de date / Aucune données n'a été trouvé",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DashboardResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Erreur dans la recherche de données")
    })
    DashboardResponse getDasbordData(@RequestBody DashboardRequest request);
}
