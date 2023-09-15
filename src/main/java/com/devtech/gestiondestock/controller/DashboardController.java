package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.DashboardApi;
import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;
import com.devtech.gestiondestock.dto.dasboard.DashboardResponse;
import com.devtech.gestiondestock.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luca
 */
@RestController
public class DashboardController implements DashboardApi {

    DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public DashboardResponse getDasboardData(DashboardRequest request) {
        return this.dashboardService.getDashboardData(request);
    }
}
