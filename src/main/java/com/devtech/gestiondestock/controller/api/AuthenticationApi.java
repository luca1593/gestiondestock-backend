package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.auth.AuthenticationRequest;
import com.devtech.gestiondestock.dto.auth.AuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.devtech.gestiondestock.utils.Constants.AUTHENTICATION_ENDPOINT;

/**
 * @author luca
 */
@CrossOrigin(origins = "*", originPatterns = "*")
public interface AuthenticationApi {
    @RequestMapping(AUTHENTICATION_ENDPOINT + "/authenticate")
    @Operation(summary = "Connextion",
            description = "Cette methode permet de se logger"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access autoriser",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EntrepriseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Access autoriser")
    })
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request);
}
