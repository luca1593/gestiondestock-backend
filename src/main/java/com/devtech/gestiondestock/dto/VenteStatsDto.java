package com.devtech.gestiondestock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenteStatsDto {
    private String periode;
    private Long nbVentes;
    private BigDecimal totalVentes;
    private BigDecimal chiffreAffaires;
}
