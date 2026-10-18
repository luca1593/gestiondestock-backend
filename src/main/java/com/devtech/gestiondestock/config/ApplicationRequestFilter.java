package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.services.auth.ApplicationUserDetailsService;
import com.devtech.gestiondestock.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author luca
 *
 * <p>Filtre d'authentification JWT. Un jeton absent, expiré ou falsifié n'entraîne
 * plus d'erreur 500 : la requête n'est simplement pas authentifiée et c'est la
 * chaîne de sécurité Spring qui décide (401 sur un endpoint protégé, accès autorisé
 * sur un endpoint public).</p>
 */
@Component
public class ApplicationRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ApplicationUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            String userEmail = null;
            String jwt = null;
            String idEntreprise = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                try {
                    userEmail = this.jwtUtil.extractUsername(jwt);
                    idEntreprise = this.jwtUtil.extractIdEntreprise(jwt);
                } catch (JwtException | IllegalArgumentException e) {
                    log.warn("JWT invalide ou expiré, requête non authentifiée : {}", e.getMessage());
                    userEmail = null;
                    jwt = null;
                    idEntreprise = null;
                }
            }

            if (idEntreprise != null) {
                MDC.put("idEntreprise", idEntreprise);
            }
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (this.jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            if (userEmail != null) {
                MDC.put("userEmail", userEmail);
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("idEntreprise");
            MDC.remove("userEmail");
        }
    }
}
