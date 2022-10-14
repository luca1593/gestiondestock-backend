package com.devtech.gestiondestock.services.auth;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.model.Utilisateur;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.repository.UtilisateurRepository;
import com.devtech.gestiondestock.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    @Autowired
    private UtilisateurService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UtilisateurDto utilisateurDto = service.findByEmailUtilisateur(email);

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
