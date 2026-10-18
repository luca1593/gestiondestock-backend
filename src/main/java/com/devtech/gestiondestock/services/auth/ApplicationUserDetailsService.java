package com.devtech.gestiondestock.services.auth;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.services.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luca
 *
 * <p>Charge l'utilisateur pour l'authentification.</p>
 *
 * <p>Toute anomalie (email inconnu, email vide, utilisateur sans entreprise ou sans
 * rôle) est traduite en {@link UsernameNotFoundException} : le
 * {@code DaoAuthenticationProvider} la convertit en {@code BadCredentialsException}
 * (HTTP 400, code {@code BAD_CREDENTIALS}) et n'expose jamais l'existence d'un compte.
 * On évite ainsi les {@code NullPointerException} qui produisaient un HTTP 500.</p>
 */
@Service("authentication")
@Slf4j
public class ApplicationUserDetailsService implements UserDetailsService {
    @Autowired
    private UtilisateurService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UtilisateurDto utilisateurDto;
        try {
            utilisateurDto = this.service.findByEmailForAuthentication(email);
        } catch (EntityNotFoundException e) {
            log.warn("Authentification refusée : aucun utilisateur pour l'email '{}'", email);
            throw new UsernameNotFoundException("Utilisateur introuvable pour l'email : " + email, e);
        }

        if (utilisateurDto == null) {
            log.warn("Authentification refusée : email vide ou inconnu '{}'", email);
            throw new UsernameNotFoundException("Utilisateur introuvable pour l'email : " + email);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (utilisateurDto.getRoles() != null) {
            utilisateurDto.getRoles().forEach(role -> authorities.add(
                    new SimpleGrantedAuthority(role.getRoleNom()))
            );
        }

        if (utilisateurDto.getEntreprise() == null || utilisateurDto.getEntreprise().getId() == null) {
            log.warn("Authentification refusée : l'utilisateur '{}' n'est rattaché à aucune entreprise", email);
            throw new UsernameNotFoundException("Utilisateur sans entreprise : " + email);
        }

        if (!StringUtils.hasText(utilisateurDto.getMotDePasse())) {
            log.warn("Authentification refusée : l'utilisateur '{}' n'a pas de mot de passe", email);
            throw new UsernameNotFoundException("Utilisateur sans mot de passe : " + email);
        }

        return new ExtendedUser(utilisateurDto.getEmail(),
                utilisateurDto.getMotDePasse(),
                utilisateurDto.getEntreprise().getId(), authorities
        );
    }
}
