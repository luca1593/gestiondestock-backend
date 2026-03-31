package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AlertStockDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.model.AlertStock;
import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.repository.AlertStockRepository;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.services.AlertStockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AlertStockServiceImpl implements AlertStockService {

    private final AlertStockRepository alertStockRepository;
    private final ArticleRepository articleRepository;

    @Override
    public AlertStockDto save(AlertStockDto dto) {
        return AlertStockDto.fromEntity(
                alertStockRepository.save(toEntity(dto))
        );
    }

    @Override
    public AlertStockDto findById(Integer id) {
        Optional<AlertStock> alert = alertStockRepository.findById(id);
        return alert.map(AlertStockDto::fromEntity).orElseThrow(() ->
                new EntityNotFoundException("Alerte stock non trouvee", ErrorsCode.ARTICLE_NOT_FOUND));
    }

    @Override
    public List<AlertStockDto> findAllByEntreprise(Integer identreprise) {
        return alertStockRepository.findByIdentrepriseAndActiveTrue(identreprise)
                .stream().map(AlertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AlertStockDto> findAlertesActives(Integer identreprise) {
        checkAndCreateAlerts(identreprise);
        return alertStockRepository.findByIdentrepriseAndActiveTrue(identreprise)
                .stream().map(AlertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AlertStockDto> findByNiveauAlerte(String niveauAlerte) {
        return alertStockRepository.findByNiveauAlerte(niveauAlerte)
                .stream().map(AlertStockDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        alertStockRepository.deleteById(id);
    }

    @Override
    public void checkAndCreateAlerts(Integer identreprise) {
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            if (article.getEntreprise() != null && article.getEntreprise().getId() != null && article.getEntreprise().getId().equals(identreprise)) {
                Double stock = article.getStock() != null ? article.getStock().doubleValue() : 0.0;
                String niveau = null;
                if (stock <= 0) {
                    niveau = "CRITIQUE";
                } else if (stock <= 5) {
                    niveau = "BAS";
                } else if (stock <= 10) {
                    niveau = "MOYEN";
                }

                if (niveau != null) {
                    List<AlertStock> existantes = alertStockRepository.findByArticleIdAndActive(article.getId());
                    if (existantes.isEmpty()) {
                        AlertStock alert = new AlertStock();
                        alert.setArticleId(article.getId());
                        alert.setDesignation(article.getDesignation());
                        alert.setStockActuel(stock);
                        alert.setSeuilMinimum(5.0);
                        alert.setSeuilCritique(0.0);
                        alert.setNiveauAlerte(niveau);
                        alert.setActive(true);
                        alert.setIdentreprise(identreprise);
                        alertStockRepository.save(alert);
                    }
                }
            }
        }
    }

    private AlertStock toEntity(AlertStockDto dto) {
        AlertStock alert = new AlertStock();
        alert.setId(dto.getId());
        alert.setArticleId(dto.getArticleId());
        alert.setDesignation(dto.getDesignation());
        alert.setStockActuel(dto.getStockActuel());
        alert.setSeuilMinimum(dto.getSeuilMinimum());
        alert.setSeuilCritique(dto.getSeuilCritique());
        alert.setNiveauAlerte(dto.getNiveauAlerte());
        alert.setActive(dto.getActive());
        alert.setIdentreprise(dto.getIdentreprise());
        return alert;
    }
}
