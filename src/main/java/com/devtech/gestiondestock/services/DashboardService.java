package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;
import com.devtech.gestiondestock.dto.dasboard.DashboardResponse;

/**
 * @author luca
 */
public interface DashboardService {
    DashboardResponse getDashboardData(DashboardRequest request);
}
