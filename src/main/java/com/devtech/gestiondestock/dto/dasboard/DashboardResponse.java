package com.devtech.gestiondestock.dto.dasboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author luca
 */
@Data
@Builder
public class DashboardResponse {

    private Map<String, BigDecimal> mapDataDashboard;

}
