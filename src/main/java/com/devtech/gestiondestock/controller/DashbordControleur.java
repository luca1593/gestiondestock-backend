package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.DashbordApi;
import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;
import com.devtech.gestiondestock.dto.dasboard.DashboardResponse;
import com.devtech.gestiondestock.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luca
 */
@RestController
public class DashbordControleur implements DashbordApi {

    DashboardService dashboardService;

    @Autowired
    public DashbordControleur(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public DashboardResponse getDasbordData(DashboardRequest request) {
        return this.dashboardService.getDashboardData(request);
    }
}
