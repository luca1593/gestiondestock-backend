package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ArticleStatsDto;
import com.devtech.gestiondestock.dto.DashboardStatsDto;
import com.devtech.gestiondestock.dto.VenteStatsDto;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.*;
import com.devtech.gestiondestock.services.DashboardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleRepository articleRepository;
    private final ClientRepository clientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;

    @Override
    public DashboardStatsDto getGlobalStats(Integer identreprise) {
        List<Article> articles = articleRepository.findAllByIdentreprise(identreprise);
        List<Client> clients = clientRepository.findAllByIdentreprise(identreprise);
        List<Fournisseur> fournisseurs = fournisseurRepository.findAllByIdentreprise(identreprise);
        List<CommandeClient> commandesClient = commandeClientRepository.findAllByIdentreprise(identreprise);
        List<CommandeFournisseur> commandesFournisseur = commandeFournisseurRepository.findAllByIdentreprise(identreprise);
        List<Vente> ventes = venteRepository.findAllByIdentreprise(identreprise);

        BigDecimal valeurStock = articles.stream()
                .map(a -> Optional.ofNullable(a.getPrixUnitaireht()).orElse(BigDecimal.ZERO)
                        .multiply(Optional.ofNullable(a.getStock()).orElse(BigDecimal.ZERO)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal chiffreAffaires = ligneVenteRepository.findAllByIdentreprise(identreprise).stream()
                .map(lv -> Optional.ofNullable(lv.getQuantite()).orElse(BigDecimal.ZERO)
                        .multiply(Optional.ofNullable(lv.getPrixUnitaire()).orElse(BigDecimal.ZERO)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long articlesStockBas = articles.stream()
                .filter(a -> Optional.ofNullable(a.getStock()).orElse(BigDecimal.ZERO).compareTo(BigDecimal.valueOf(5)) <= 0)
                .count();

        long commandesEnAttente = commandesClient.stream()
                .filter(cc -> cc.getEtatcommande() == EtatCommande.EN_PREPARATION
                        || cc.getEtatcommande() == EtatCommande.VALIDEE)
                .count();

        return DashboardStatsDto.builder()
                .totalArticles((long) articles.size())
                .totalClients((long) clients.size())
                .totalFournisseurs((long) fournisseurs.size())
                .totalCommandesClient((long) commandesClient.size())
                .totalCommandesFournisseur((long) commandesFournisseur.size())
                .totalVentes((long) ventes.size())
                .chiffreAffaires(chiffreAffaires)
                .valeurStock(valeurStock)
                .articlesStockBas(articlesStockBas)
                .commandesEnAttente(commandesEnAttente)
                .build();
    }

    @Override
    public List<ArticleStatsDto> getTopArticles(Integer identreprise, int limit) {
        List<Article> articles = articleRepository.findAllByIdentreprise(identreprise);
        List<LigneVente> lignesVente = ligneVenteRepository.findAllByIdentreprise(identreprise);
        List<LigneCommandeClient> lignesCC = ligneCommandeClientRepository.findAll();

        return articles.stream()
                .map(article -> {
                    long nbVentes = lignesVente.stream().filter(lv -> lv.getArticle() != null && lv.getArticle().getId().equals(article.getId())).count();
                    long nbCC = lignesCC.stream().filter(lc -> lc.getArticle() != null && lc.getArticle().getId().equals(article.getId())).count();

                    return ArticleStatsDto.builder()
                            .articleId(article.getId())
                            .codeArticle(article.getCodeArticle())
                            .designation(article.getDesignation())
                            .stock(article.getStock())
                            .prixUnitaire(article.getPrixUnitaireht())
                            .category(article.getCategory() != null ? article.getCategory().getCode() : "N/A")
                            .nbVentes(nbVentes)
                            .nbCommandesClient(nbCC)
                            .build();
                })
                .sorted(Comparator.comparingLong(ArticleStatsDto::getNbVentes).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteStatsDto> getVentesParPeriode(Integer identreprise, Instant debut, Instant fin) {
        List<Vente> ventes = venteRepository.findAllByIdentreprise(identreprise).stream()
                .filter(v -> v.getDateVente() != null && !v.getDateVente().isBefore(debut) && !v.getDateVente().isAfter(fin))
                .collect(Collectors.toList());

        Map<String, List<Vente>> groupedByDate = ventes.stream()
                .collect(Collectors.groupingBy(v -> {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            .withZone(ZoneId.systemDefault());
                    return fmt.format(v.getDateVente());
                }));

        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(Vente::getId).map(id -> ligneVenteRepository.findAllByVenteId(id))
                            .flatMap(List::stream)
                            .map(lv -> Optional.ofNullable(lv.getQuantite()).orElse(BigDecimal.ZERO)
                                    .multiply(Optional.ofNullable(lv.getPrixUnitaire()).orElse(BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return VenteStatsDto.builder()
                            .periode(entry.getKey())
                            .nbVentes((long) entry.getValue().size())
                            .totalVentes(total)
                            .chiffreAffaires(total)
                            .build();
                })
                .sorted(Comparator.comparing(VenteStatsDto::getPeriode))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenteStatsDto> getChiffreAffairesParMois(Integer identreprise, Integer annee) {
        List<Vente> ventes = venteRepository.findAllByIdentreprise(identreprise).stream()
                .filter(v -> v.getDateVente() != null)
                .filter(v -> {
                    int year = v.getDateVente().atZone(ZoneId.systemDefault()).getYear();
                    return year == annee;
                })
                .collect(Collectors.toList());

        Map<String, List<Vente>> groupedByMonth = ventes.stream()
                .collect(Collectors.groupingBy(v -> {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            .withZone(ZoneId.systemDefault());
                    return fmt.format(v.getDateVente());
                }));

        return groupedByMonth.entrySet().stream()
                .map(entry -> {
                    BigDecimal ca = entry.getValue().stream()
                            .map(Vente::getId).map(id -> ligneVenteRepository.findAllByVenteId(id))
                            .flatMap(List::stream)
                            .map(lv -> Optional.ofNullable(lv.getQuantite()).orElse(BigDecimal.ZERO)
                                    .multiply(Optional.ofNullable(lv.getPrixUnitaire()).orElse(BigDecimal.ZERO)))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return VenteStatsDto.builder()
                            .periode(entry.getKey())
                            .nbVentes((long) entry.getValue().size())
                            .chiffreAffaires(ca)
                            .build();
                })
                .sorted(Comparator.comparing(VenteStatsDto::getPeriode))
                .collect(Collectors.toList());
    }
}
