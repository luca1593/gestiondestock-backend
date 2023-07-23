package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.AuthenticationApi;
import com.devtech.gestiondestock.dto.auth.AuthenticationRequest;
import com.devtech.gestiondestock.dto.auth.AuthenticationResponse;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.services.auth.ApplicationUserDetailsService;
import com.devtech.gestiondestock.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author luca
 */
@RestController
public class AuthenticationController implements AuthenticationApi {
    @Autowired
    private ApplicationUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getLogin());
        final String jwt = this.jwtUtil.generateToken((ExtendedUser) userDetails);
        return ResponseEntity.ok(AuthenticationResponse.builder().accessTokeen(jwt).build());
    }
}
