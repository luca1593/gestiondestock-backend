package com.devtech.gestiondestock.dto.dasboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

/**
 * @author luca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequest {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Calendar startDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Calendar endDate;

}
