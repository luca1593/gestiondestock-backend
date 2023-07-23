package com.devtech.gestiondestock.services.auth;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.services.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luca
 */
@Service("authentication")
@Slf4j
public class ApplicationUserDetailsService implements UserDetailsService {
    @Autowired
    private UtilisateurService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UtilisateurDto utilisateurDto = this.service.findByEmailUtilisateur(email);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        utilisateurDto.getRoles().forEach(role -> authorities.add(
                new SimpleGrantedAuthority(role.getRoleNom()))
        );

        return new ExtendedUser(utilisateurDto.getEmail(),
                utilisateurDto.getMotDePasse(),
                utilisateurDto.getEntreprise().getId(), authorities
        );
    }
}
