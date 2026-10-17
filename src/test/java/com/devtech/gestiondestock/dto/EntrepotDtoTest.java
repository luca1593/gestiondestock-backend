package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link EntrepotDto} (aucune methode fromEntity/toEntity).
 *
 * Particularite : ce DTO ne possede pas de builder. Le champ adresse
 * porte directement l'entite {@link Adresse} (et non un AdresseDto).
 */
class EntrepotDtoTest {

    private Adresse sampleAdresse() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("1 rue du Depot");
        adresse.setVille("Lyon");
        adresse.setCodePostal("69001");
        adresse.setPays("France");
        return adresse;
    }

    private EntrepotDto sample() {
        EntrepotDto dto = new EntrepotDto();
        dto.setId(3);
        dto.setCode("ENT-01");
        dto.setNom("Entrepot principal");
        dto.setDescription("Stock central");
        dto.setAdresse(sampleAdresse());
        dto.setEstPrincipal(Boolean.TRUE);
        dto.setEntrepriseId(9);
        dto.setEntrepriseNom("Ma societe");
        return dto;
    }

    @Test
    @DisplayName("les setters renseignent tous les champs")
    void settersShouldFillAllFields() {
        EntrepotDto dto = sample();

        assertEquals(Integer.valueOf(3), dto.getId());
        assertEquals("ENT-01", dto.getCode());
        assertEquals("Entrepot principal", dto.getNom());
        assertEquals("Stock central", dto.getDescription());
        assertEquals("Lyon", dto.getAdresse().getVille());
        assertEquals(Boolean.TRUE, dto.getEstPrincipal());
        assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
        assertEquals("Ma societe", dto.getEntrepriseNom());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        EntrepotDto a = sample();
        EntrepotDto b = sample();
        EntrepotDto c = new EntrepotDto();
        c.setId(99);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("le constructeur par defaut laisse les champs a null")
    void defaultConstructorShouldLeaveFieldsNull() {
        EntrepotDto dto = new EntrepotDto();

        assertNull(dto.getId());
        assertNull(dto.getCode());
        assertNull(dto.getAdresse());
        assertNull(dto.getEstPrincipal());
        assertNull(dto.getEntrepriseId());
    }
}
