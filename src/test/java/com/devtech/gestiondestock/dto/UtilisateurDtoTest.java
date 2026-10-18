package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.model.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link UtilisateurDto}.
 */
class UtilisateurDtoTest {

    private Instant sampleNaissance() {
        return Instant.parse("1990-05-20T00:00:00Z");
    }

    private AdresseDto sampleAdresseDto() {
        return AdresseDto.builder()
                .adresse1("1 rue A")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private Adresse sampleAdresse() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("1 rue A");
        adresse.setVille("Paris");
        adresse.setCodePostal("75001");
        adresse.setPays("France");
        return adresse;
    }

    private EntrepriseDto sampleEntrepriseDto() {
        return EntrepriseDto.builder().id(9).nom("Ma societe").build();
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private UtilisateurDto sampleDto() {
        return UtilisateurDto.builder()
                .id(1)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@example.com")
                .dateDeNaissance(sampleNaissance())
                .motDePasse("secret")
                .adresse(sampleAdresseDto())
                .photo("user.png")
                .roles(List.of(RoleDto.builder().id(80).roleNom("ADMIN").build()))
                .entreprise(sampleEntrepriseDto())
                .build();
    }

    private Utilisateur sampleEntity() {
        Role role = new Role();
        role.setId(80);
        role.setRoleNom("ADMIN");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setEmail("jean.dupont@example.com");
        utilisateur.setDateDeNaissance(sampleNaissance());
        utilisateur.setMotDePasse("secret");
        utilisateur.setAdresse(sampleAdresse());
        utilisateur.setPhoto("user.png");
        utilisateur.setRoles(List.of(role));
        utilisateur.setEntreprise(sampleEntreprise());
        return utilisateur;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(UtilisateurDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs, l'adresse, l'entreprise et les roles")
        void shouldMapAllFields() {
            UtilisateurDto dto = UtilisateurDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(1), dto.getId());
            assertEquals("Dupont", dto.getNom());
            assertEquals("Jean", dto.getPrenom());
            assertEquals("jean.dupont@example.com", dto.getEmail());
            assertEquals(sampleNaissance(), dto.getDateDeNaissance());
            assertEquals("secret", dto.getMotDePasse());
            assertEquals("user.png", dto.getPhoto());
            assertNotNull(dto.getAdresse());
            assertEquals("Paris", dto.getAdresse().getVille());
            assertNotNull(dto.getEntreprise());
            assertEquals("Ma societe", dto.getEntreprise().getNom());
            assertEquals(1, dto.getRoles().size());
            assertEquals("ADMIN", dto.getRoles().get(0).getRoleNom());
        }

        @Test
        @DisplayName("tolerant aux associations et roles nuls")
        void shouldTolerateNullAssociations() {
            Utilisateur utilisateur = sampleEntity();
            utilisateur.setAdresse(null);
            utilisateur.setEntreprise(null);
            utilisateur.setRoles(null);

            UtilisateurDto dto = UtilisateurDto.fromEntity(utilisateur);

            assertNull(dto.getAdresse());
            assertNull(dto.getEntreprise());
            assertNull(dto.getRoles());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(UtilisateurDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs, l'adresse, l'entreprise et les roles")
        void shouldMapAllFields() {
            Utilisateur utilisateur = UtilisateurDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(1), utilisateur.getId());
            assertEquals("Dupont", utilisateur.getNom());
            assertEquals("Jean", utilisateur.getPrenom());
            assertEquals("jean.dupont@example.com", utilisateur.getEmail());
            assertEquals(sampleNaissance(), utilisateur.getDateDeNaissance());
            assertEquals("secret", utilisateur.getMotDePasse());
            assertEquals("user.png", utilisateur.getPhoto());
            assertNotNull(utilisateur.getAdresse());
            assertEquals("Paris", utilisateur.getAdresse().getVille());
            assertNotNull(utilisateur.getEntreprise());
            assertEquals("Ma societe", utilisateur.getEntreprise().getNom());
            assertEquals(1, utilisateur.getRoles().size());
            assertEquals("ADMIN", utilisateur.getRoles().get(0).getRoleNom());
        }

        @Test
        @DisplayName("tolerant aux roles nuls")
        void shouldTolerateNullRoles() {
            UtilisateurDto dto = sampleDto();
            dto.setRoles(null);

            assertNull(UtilisateurDto.toEntity(dto).getRoles());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        UtilisateurDto original = sampleDto();
        UtilisateurDto result = UtilisateurDto.fromEntity(UtilisateurDto.toEntity(original));

        assertEquals(original, result);
    }
}
