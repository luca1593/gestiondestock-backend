package com.devtech.gestiondestock.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'intégration du dispositif de sécurité HTTP (chaîne de filtres réelle).
 *
 * <p>Valide les correctifs C-2 (login), E-3 (email inconnu) et E-4 (jeton invalide) :
 * aucune de ces situations ne doit produire un HTTP 500, et un endpoint protégé sans
 * jeton doit répondre 401 (et non 403).</p>
 *
 * <p>Le bean {@code DataInitializer} crée au démarrage un utilisateur
 * {@code admin@default.com / admin123} lorsque la base est vide.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitytest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AUTH_URL = "/v1/auth/authenticate";
    private static final String PROTECTED_URL = "/v1/articles/all";

    @Test
    void endpointProtege_sansJeton_retourne401() throws Exception {
        mockMvc.perform(get(PROTECTED_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtege_avecJetonFalsifie_retourne401EtNon500() throws Exception {
        mockMvc.perform(get(PROTECTED_URL).header("Authorization", "Bearer pas.un.jwt.valide"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_emailInconnu_retourne400BadCredentialsEtNon404() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"inconnu@test.com\",\"password\":\"peu-importe\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorsCode").exists());
    }

    @Test
    void login_corpsVide_retourne400EtNon500() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_motDePasseIncorrect_retourne400() throws Exception {
        mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin@default.com\",\"password\":\"mauvais\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_valide_retourneUnJetonQuiDonneAccesAuxEndpointsProteges() throws Exception {
        String body = mockMvc.perform(post(AUTH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"admin@default.com\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(body);
        String accessToken = node.get("accessToken").asText();

        mockMvc.perform(get(PROTECTED_URL).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}
