package com.devtech.gestiondestock.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link VenteStatsDto} (aucune conversion entite).
 */
class VenteStatsDtoTest {

    private VenteStatsDto sample() {
        return VenteStatsDto.builder()
                .periode("2026-10")
                .nbVentes(42L)
                .totalVentes(new BigDecimal("12000.00"))
                .chiffreAffaires(new BigDecimal("9500.50"))
                .build();
    }

    @Test
    @DisplayName("le builder renseigne tous les champs")
    void builderShouldFillAllFields() {
        VenteStatsDto dto = sample();

        assertEquals("2026-10", dto.getPeriode());
        assertEquals(Long.valueOf(42L), dto.getNbVentes());
        assertEquals(new BigDecimal("12000.00"), dto.getTotalVentes());
        assertEquals(new BigDecimal("9500.50"), dto.getChiffreAffaires());
    }

    @Test
    @DisplayName("les setters modifient les champs")
    void settersShouldUpdateFields() {
        VenteStatsDto dto = new VenteStatsDto();
        dto.setPeriode("2026-11");
        dto.setNbVentes(1L);
        dto.setTotalVentes(new BigDecimal("10"));
        dto.setChiffreAffaires(new BigDecimal("20"));

        assertEquals("2026-11", dto.getPeriode());
        assertEquals(Long.valueOf(1L), dto.getNbVentes());
        assertEquals(new BigDecimal("10"), dto.getTotalVentes());
        assertEquals(new BigDecimal("20"), dto.getChiffreAffaires());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        VenteStatsDto a = sample();
        VenteStatsDto b = sample();
        VenteStatsDto c = VenteStatsDto.builder().periode("2026-01").build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("le constructeur par defaut laisse les champs a null")
    void defaultConstructorShouldLeaveFieldsNull() {
        VenteStatsDto dto = new VenteStatsDto();

        assertNull(dto.getPeriode());
        assertNull(dto.getNbVentes());
        assertNull(dto.getTotalVentes());
        assertNull(dto.getChiffreAffaires());
    }

    @Test
    @DisplayName("le constructeur avec tous les arguments renseigne les champs")
    void allArgsConstructorShouldFillFields() {
        VenteStatsDto dto = new VenteStatsDto("2026-10", 42L, new BigDecimal("10"), new BigDecimal("20"));

        assertEquals("2026-10", dto.getPeriode());
        assertEquals(Long.valueOf(42L), dto.getNbVentes());
        assertEquals(new BigDecimal("10"), dto.getTotalVentes());
        assertEquals(new BigDecimal("20"), dto.getChiffreAffaires());
    }
}
