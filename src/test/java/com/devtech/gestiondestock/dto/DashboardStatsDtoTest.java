package com.devtech.gestiondestock.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link DashboardStatsDto} (aucune conversion entite).
 */
class DashboardStatsDtoTest {

    private DashboardStatsDto sample() {
        return DashboardStatsDto.builder()
                .totalArticles(120L)
                .totalClients(30L)
                .totalFournisseurs(12L)
                .totalCommandesClient(48L)
                .totalCommandesFournisseur(20L)
                .totalVentes(300L)
                .chiffreAffaires(new BigDecimal("45000.00"))
                .valeurStock(new BigDecimal("12000.50"))
                .articlesStockBas(5L)
                .commandesEnAttente(7L)
                .build();
    }

    @Test
    @DisplayName("le builder renseigne tous les champs")
    void builderShouldFillAllFields() {
        DashboardStatsDto dto = sample();

        assertEquals(Long.valueOf(120L), dto.getTotalArticles());
        assertEquals(Long.valueOf(30L), dto.getTotalClients());
        assertEquals(Long.valueOf(12L), dto.getTotalFournisseurs());
        assertEquals(Long.valueOf(48L), dto.getTotalCommandesClient());
        assertEquals(Long.valueOf(20L), dto.getTotalCommandesFournisseur());
        assertEquals(Long.valueOf(300L), dto.getTotalVentes());
        assertEquals(new BigDecimal("45000.00"), dto.getChiffreAffaires());
        assertEquals(new BigDecimal("12000.50"), dto.getValeurStock());
        assertEquals(Long.valueOf(5L), dto.getArticlesStockBas());
        assertEquals(Long.valueOf(7L), dto.getCommandesEnAttente());
    }

    @Test
    @DisplayName("les setters modifient les champs")
    void settersShouldUpdateFields() {
        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setTotalArticles(1L);
        dto.setTotalClients(2L);
        dto.setTotalFournisseurs(3L);
        dto.setTotalCommandesClient(4L);
        dto.setTotalCommandesFournisseur(5L);
        dto.setTotalVentes(6L);
        dto.setChiffreAffaires(new BigDecimal("10"));
        dto.setValeurStock(new BigDecimal("20"));
        dto.setArticlesStockBas(7L);
        dto.setCommandesEnAttente(8L);

        assertEquals(Long.valueOf(1L), dto.getTotalArticles());
        assertEquals(Long.valueOf(2L), dto.getTotalClients());
        assertEquals(Long.valueOf(3L), dto.getTotalFournisseurs());
        assertEquals(Long.valueOf(4L), dto.getTotalCommandesClient());
        assertEquals(Long.valueOf(5L), dto.getTotalCommandesFournisseur());
        assertEquals(Long.valueOf(6L), dto.getTotalVentes());
        assertEquals(new BigDecimal("10"), dto.getChiffreAffaires());
        assertEquals(new BigDecimal("20"), dto.getValeurStock());
        assertEquals(Long.valueOf(7L), dto.getArticlesStockBas());
        assertEquals(Long.valueOf(8L), dto.getCommandesEnAttente());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        DashboardStatsDto a = sample();
        DashboardStatsDto b = sample();
        DashboardStatsDto c = DashboardStatsDto.builder().totalArticles(0L).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("le constructeur par defaut laisse les champs a null")
    void defaultConstructorShouldLeaveFieldsNull() {
        DashboardStatsDto dto = new DashboardStatsDto();

        assertNull(dto.getTotalArticles());
        assertNull(dto.getChiffreAffaires());
        assertNull(dto.getCommandesEnAttente());
    }

    @Test
    @DisplayName("le constructeur avec tous les arguments renseigne les champs")
    void allArgsConstructorShouldFillFields() {
        DashboardStatsDto dto = new DashboardStatsDto(
                1L, 2L, 3L, 4L, 5L, 6L,
                new BigDecimal("10"), new BigDecimal("20"), 7L, 8L);

        assertEquals(Long.valueOf(1L), dto.getTotalArticles());
        assertEquals(new BigDecimal("20"), dto.getValeurStock());
        assertEquals(Long.valueOf(8L), dto.getCommandesEnAttente());
    }
}
