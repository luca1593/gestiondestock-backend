package com.devtech.gestiondestock.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @JsonProperty("email")
    private String login;

    @JsonProperty("motDePasse")
    private String password;

}
