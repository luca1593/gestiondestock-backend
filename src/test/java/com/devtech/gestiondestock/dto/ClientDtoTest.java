package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link ClientDto}.
 */
class ClientDtoTest {

    private AdresseDto sampleAdresseDto() {
        return AdresseDto.builder()
                .adresse1("1 rue A")
                .adresse2("Appartement 2")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private Adresse sampleAdresse() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("1 rue A");
        adresse.setAdresse2("Appartement 2");
        adresse.setVille("Paris");
        adresse.setCodePostal("75001");
        adresse.setPays("France");
        return adresse;
    }

    private ClientDto sampleDto() {
        return ClientDto.builder()
                .id(3)
                .nom("Dupont")
                .prenom("Jean")
                .adresse(sampleAdresseDto())
                .photo("client.png")
                .email("jean.dupont@example.com")
                .numTel("0102030405")
                .identreprise(9)
                .build();
    }

    private Client sampleEntity() {
        Client client = new Client();
        client.setId(3);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setAdresse(sampleAdresse());
        client.setPhoto("client.png");
        client.setEmail("jean.dupont@example.com");
        client.setNumTel("0102030405");
        client.setIdentreprise(9);
        return client;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(ClientDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            ClientDto dto = ClientDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(3), dto.getId());
            assertEquals("Dupont", dto.getNom());
            assertEquals("Jean", dto.getPrenom());
            assertEquals("client.png", dto.getPhoto());
            assertEquals("jean.dupont@example.com", dto.getEmail());
            assertEquals("0102030405", dto.getNumTel());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getAdresse());
            assertEquals("Paris", dto.getAdresse().getVille());
        }

        @Test
        @DisplayName("tolerant au client dont l'adresse est nulle")
        void shouldTolerateNullAdresse() {
            Client client = sampleEntity();
            client.setAdresse(null);

            assertNull(ClientDto.fromEntity(client).getAdresse());
        }

        @Test
        @DisplayName("ne mappe pas la liste commandeClients")
        void shouldNotMapCommandes() {
            assertNull(ClientDto.fromEntity(sampleEntity()).getCommandeClients());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(ClientDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            Client client = ClientDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(3), client.getId());
            assertEquals("Dupont", client.getNom());
            assertEquals("Jean", client.getPrenom());
            assertEquals("client.png", client.getPhoto());
            assertEquals("jean.dupont@example.com", client.getEmail());
            assertEquals("0102030405", client.getNumTel());
            assertEquals(Integer.valueOf(9), client.getIdentreprise());
            assertNotNull(client.getAdresse());
            assertEquals("Paris", client.getAdresse().getVille());
        }

        @Test
        @DisplayName("tolerant au DTO dont l'adresse est nulle")
        void shouldTolerateNullAdresse() {
            ClientDto dto = sampleDto();
            dto.setAdresse(null);

            assertNull(ClientDto.toEntity(dto).getAdresse());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        ClientDto original = sampleDto();
        ClientDto result = ClientDto.fromEntity(ClientDto.toEntity(original));

        assertEquals(original, result);
    }
}
