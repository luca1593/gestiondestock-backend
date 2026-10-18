package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.services.auth.ApplicationUserDetailsService;
import com.devtech.gestiondestock.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito) de {@link ApplicationRequestFilter}.
 *
 * <p>Point clé (correctif E-4) : un jeton absent, expiré ou falsifié ne doit plus
 * produire d'erreur 500 — la requête est simplement non authentifiée et la chaîne
 * de filtres continue.</p>
 */
@ExtendWith(MockitoExtension.class)
class ApplicationRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private ApplicationUserDetailsService userDetailsService;

    @InjectMocks
    private ApplicationRequestFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    private MockHttpServletRequest requestAvecJeton(String jeton) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jeton);
        return request;
    }

    @Test
    void jetonValide_authentifieEtRenseigneLeMdc() throws Exception {
        UserDetails userDetails = new ExtendedUser("admin@test.com", "pwd", 3,
                Collections.singletonList(new SimpleGrantedAuthority("Admin")));
        when(jwtUtil.extractUsername("jeton")).thenReturn("admin@test.com");
        when(jwtUtil.extractIdEntreprise("jeton")).thenReturn("3");
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken("jeton", userDetails)).thenReturn(true);

        AtomicReference<String> tenantPendantLaChaine = new AtomicReference<>();
        AtomicReference<String> emailPendantLaChaine = new AtomicReference<>();

        filter.doFilter(requestAvecJeton("jeton"), new MockHttpServletResponse(),
                (req, res) -> {
                    tenantPendantLaChaine.set(MDC.get("idEntreprise"));
                    emailPendantLaChaine.set(MDC.get("userEmail"));
                });

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("3", tenantPendantLaChaine.get());
        assertEquals("admin@test.com", emailPendantLaChaine.get());
        // Le MDC est nettoyé après la requête (thread de pool réutilisé).
        assertNull(MDC.get("idEntreprise"));
        assertNull(MDC.get("userEmail"));
    }

    @Test
    void jetonInvalide_neLevePasDException_etNauthentifiePas() throws Exception {
        when(jwtUtil.extractUsername("falsifie")).thenThrow(new JwtException("signature invalide"));

        MockFilterChain chain = new MockFilterChain();
        assertDoesNotThrow(() ->
                filter.doFilter(requestAvecJeton("falsifie"), new MockHttpServletResponse(), chain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNull(MDC.get("idEntreprise"));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void jetonExpire_neLevePasDException_etNauthentifiePas() throws Exception {
        when(jwtUtil.extractUsername("expire")).thenThrow(new JwtException("JWT expired"));

        assertDoesNotThrow(() ->
                filter.doFilter(requestAvecJeton("expire"), new MockHttpServletResponse(), new MockFilterChain()));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void jetonMalForme_leveIllegalArgumentExceptionCapturee() throws Exception {
        when(jwtUtil.extractUsername("malforme")).thenThrow(new IllegalArgumentException("jeton vide"));

        assertDoesNotThrow(() ->
                filter.doFilter(requestAvecJeton("malforme"), new MockHttpServletResponse(), new MockFilterChain()));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void sansEnTeteAuthorization_neFaitRien_etContinueLaChaine() throws Exception {
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    void enTeteNonBearer_neFaitRien() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abcdef");

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtUtil, userDetailsService);
    }

    @Test
    void jetonValideMaisValidationEchouee_nAuthentifiePas() throws Exception {
        UserDetails userDetails = new ExtendedUser("admin@test.com", "pwd", 3, Collections.emptyList());
        when(jwtUtil.extractUsername("jeton")).thenReturn("admin@test.com");
        when(jwtUtil.extractIdEntreprise("jeton")).thenReturn("3");
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken("jeton", userDetails)).thenReturn(false);

        filter.doFilter(requestAvecJeton("jeton"), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
