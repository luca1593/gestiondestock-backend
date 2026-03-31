package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.ArticleStatsDto;
import com.devtech.gestiondestock.dto.DashboardStatsDto;
import com.devtech.gestiondestock.dto.VenteStatsDto;

import java.time.Instant;
import java.util.List;

public interface DashboardService {
    DashboardStatsDto getGlobalStats(Integer identreprise);
    List<ArticleStatsDto> getTopArticles(Integer identreprise, int limit);
    List<VenteStatsDto> getVentesParPeriode(Integer identreprise, Instant debut, Instant fin);
    List<VenteStatsDto> getChiffreAffairesParMois(Integer identreprise, Integer annee);
}
