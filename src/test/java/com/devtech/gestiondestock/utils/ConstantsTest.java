package com.devtech.gestiondestock.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires des constantes d'URL.
 *
 * Verifie la coherence des routes exposees : prefixe commun, absence de
 * double slash (bug corrige sur les endpoints entreprise), placeholders.
 */
class ConstantsTest {

    @Test
    @DisplayName("Le prefixe racine est v1")
    void rootShouldBeV1() {
        assertEquals("v1", Constants.APP_ROOT);
        assertEquals("v1/auth", Constants.AUTHENTICATION_ENDPOINT);
    }

    @Test
    @DisplayName("Aucune route ne contient de double slash")
    void noRouteShouldContainDoubleSlash() {
        String[] routes = {
                Constants.COMMANDE_FOURNISSEUR_ENDPOINT,
                Constants.CREATE_COMMANDE_FOURNISSEUR,
                Constants.UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT,
                Constants.FIND_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT,
                Constants.FIND_COMMANDE_FOURNISSEUR_BY_CODE_ENDPOINT,
                Constants.FIND_COMMANDE_FOURNISSEUR_BY_DATE_ENDPOINT,
                Constants.FIND_ALL_COMMANDE_FOURNISSEUR,
                Constants.DELETE_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT,
                Constants.ENTREPRISE_ENDPOINT,
                Constants.CREATE_ENTREPRISE_ENDPOINT,
                Constants.FIND_ENTREPRISE_BY_ID_ENDPOINT,
                Constants.FIND_ENTREPRISE_BY_NOM_ENDPOINT,
                Constants.FIND_ENTREPRISE_BY_MAIL_ENDPOINT,
                Constants.FIND_ALL_ENTREPRISE_ENDPOINT,
                Constants.DELETE_ENTREPRISE_BY_ID_ENDPOINT,
        };
        for (String route : routes) {
            assertFalse(route.contains("//"), "La route ne doit pas contenir '//' : " + route);
        }
    }

    @Test
    @DisplayName("Les routes entreprise par nom et par id conservent le slash de separation")
    void entrepriseRoutesShouldKeepSeparator() {
        assertTrue(Constants.FIND_ENTREPRISE_BY_NOM_ENDPOINT.startsWith("v1/entreprise/nom/"),
                "Route attendue : v1/entreprise/nom/{nomEntreprise}");
        assertTrue(Constants.DELETE_ENTREPRISE_BY_ID_ENDPOINT.startsWith("v1/entreprise/delete/"),
                "Route attendue : v1/entreprise/delete/{idEntreprise}");
    }
}
