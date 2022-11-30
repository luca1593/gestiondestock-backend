package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.auth.AuthenticationRequest;
import com.devtech.gestiondestock.dto.auth.AuthenticationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.devtech.gestiondestock.utils.Constants.AUTHENTICATION_ENDPOINT;

@Api(AUTHENTICATION_ENDPOINT)
public interface AuthenticationApi {
    @RequestMapping(AUTHENTICATION_ENDPOINT + "/authenticate")
    @ApiOperation(value = "Connextion",
            notes = "Cette methode permet de se logger",
            response = EntrepriseDto.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Access autoriser"),
            @ApiResponse(code = 404, message = "Access autoriser")
    })
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request);
}
