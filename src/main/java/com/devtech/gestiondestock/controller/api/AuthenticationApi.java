package com.devtech.gestiondestock.controller.api;

import com.devtech.gestiondestock.dto.auth.AuthenticationRequest;
import com.devtech.gestiondestock.dto.auth.AuthenticationResponse;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.devtech.gestiondestock.utils.Constants.AUTHENTICATION_ENDPOINT;

@Api(AUTHENTICATION_ENDPOINT)
public interface AuthenticationApi {
    @RequestMapping(AUTHENTICATION_ENDPOINT + "/authenticate")
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request);
}
