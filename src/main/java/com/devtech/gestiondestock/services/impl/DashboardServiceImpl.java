package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;
import com.devtech.gestiondestock.dto.dasboard.DashboardResponse;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.services.CommandeClientService;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import com.devtech.gestiondestock.services.DashboardService;
import com.devtech.gestiondestock.services.VenteService;
import com.devtech.gestiondestock.validator.DateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luca
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    CommandeClientService commandeClientService;
    CommandeFournisseurService commandeFournisseurService;
    VenteService venteService;

    @Autowired
    public DashboardServiceImpl(CommandeClientService commandeClientService, CommandeFournisseurService commandeFournisseurService, VenteService venteService) {
        this.commandeClientService = commandeClientService;
        this.commandeFournisseurService = commandeFournisseurService;
        this.venteService = venteService;
    }

    @Override
    public DashboardResponse getDashboardData(DashboardRequest request) {
        Instant endDate = Instant.now();
        String error = DateValidator.validate(request);
        BigDecimal qnt = BigDecimal.ZERO;
        BigDecimal prix = BigDecimal.ZERO;
        Map<String, BigDecimal> mapData = new HashMap<>();
        if (StringUtils.hasLength(error)) {
            log.error("Erreur sur les date du requête");
            throw new InvalidEntityException(error, ErrorsCode.BAD_REQUEST);
        }

        Instant startDate = request.getStartDate().toInstant();
        if (request.getEndDate() != null) {
            request.getEndDate().set(Calendar.HOUR, 23);
            request.getEndDate().set(Calendar.MINUTE, 59);
            request.getEndDate().set(Calendar.SECOND, 59);
            endDate = request.getEndDate().toInstant();
        }
        for (LigneCommandeClientDto dto : this.commandeClientService.findAllByDateInterval(startDate, endDate)) {
            qnt = qnt.add(dto.getQuantite());
            prix = prix.add(dto.getPrixUnitaire().multiply(dto.getQuantite()));
        }
        mapData.put("cmdclt", qnt);
        mapData.put("prixclt", prix.setScale(2));
        qnt = BigDecimal.ZERO;
        prix = BigDecimal.ZERO;

        for (LigneCommandeFournisseurDto dto : this.commandeFournisseurService.findAllByDateInterval(startDate, endDate)) {
            qnt = qnt.add(dto.getQuantite());
            prix = prix.add(dto.getPrixUnitaire().multiply(dto.getQuantite()));
        }
        mapData.put("cmdfrs", qnt);
        mapData.put("prixfrs", prix.setScale(2));
        qnt = BigDecimal.ZERO;
        prix = BigDecimal.ZERO;

        for (LigneVenteDto dto : this.venteService.findAllByDateInterval(startDate, endDate)) {
            qnt = qnt.add(dto.getQuantite());
            prix = prix.add(dto.getPrixUnitaire().multiply(dto.getQuantite()));
        }
        mapData.put("vnte", qnt);
        mapData.put("prixvnte", prix.setScale(2));

        return DashboardResponse.builder().mapDataDashboard(mapData).build();
    }
}
