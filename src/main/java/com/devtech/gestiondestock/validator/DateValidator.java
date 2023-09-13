package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.dasboard.DashboardRequest;

import java.util.Calendar;

/**
 * @author luca
 */
public class DateValidator {

    private DateValidator() {
        // Not implemented
    }

    public static String validate(DashboardRequest request) {
        StringBuilder error = new StringBuilder();
        if (request == null) {
            error.append("Aucune donnée ne corréspond à une intérval de date null");
        }

        if (request != null && request.getStartDate() == null) {
            error.append("La date de debut ne peux pas être null");
        }

        if (request != null && request.getStartDate() != null && compareDate(request.getStartDate(), request.getEndDate()) > 0) {
            error.append("La date de début doit être inférieur a la date de fin");
        }

        return error.toString();
    }

    private static int compareDate(Calendar date1, Calendar date2) {
        int result = 0;
        if (date1.after(date2)) {
            result = 1;
        }
        if (date1.before(date2)) {
            result = -1;
        }
        return result;
    }
}
