package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.model.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link RoleDto}.
 *
 * Particularite : fromEntity ne recopie pas l'utilisateur porteur du role,
 * alors que toEntity le fait. Le comportement reel est verrouille ici.
 */
class RoleDtoTest {

    private UtilisateurDto sampleUtilisateurDto() {
        return UtilisateurDto.builder().id(1).nom("Dupont").prenom("Jean").build();
    }

    private Utilisateur sampleUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        return utilisateur;
    }

    private RoleDto sampleDto() {
        return RoleDto.builder()
                .id(80)
                .roleNom("ADMIN")
                .build();
    }

    private Role sampleEntity() {
        Role role = new Role();
        role.setId(80);
        role.setRoleNom("ADMIN");
        return role;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(RoleDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie id et roleNom")
        void shouldMapFields() {
            RoleDto dto = RoleDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(80), dto.getId());
            assertEquals("ADMIN", dto.getRoleNom());
        }

        @Test
        @DisplayName("ne recopie pas l'utilisateur porteur du role")
        void shouldNotMapUtilisateur() {
            Role role = sampleEntity();
            role.setUtilisateur(sampleUtilisateur());

            assertNull(RoleDto.fromEntity(role).getUtilisateur());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(RoleDto.toEntity(null));
        }

        @Test
        @DisplayName("copie id et roleNom")
        void shouldMapFields() {
            Role role = RoleDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(80), role.getId());
            assertEquals("ADMIN", role.getRoleNom());
        }

        @Test
        @DisplayName("recopie l'utilisateur lorsqu'il est renseigne")
        void shouldMapUtilisateur() {
            RoleDto dto = sampleDto();
            dto.setUtilisateur(sampleUtilisateurDto());

            Role role = RoleDto.toEntity(dto);

            assertNotNull(role.getUtilisateur());
            assertEquals("Dupont", role.getUtilisateur().getNom());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        RoleDto original = sampleDto();
        RoleDto result = RoleDto.fromEntity(RoleDto.toEntity(original));

        assertEquals(original, result);
    }
}
