package com.devtech.gestiondestock.utils;

public interface Constants {
    String APP_ROOT = "gestiondestock/v1";
    String CREATE_ENDPOINT = "/create";
    String CODE_ENDPOINT = "/code/";
    String DATE_ENDPOINT = "/date/";
    String ALL_ENDPOINT = "/all";
    String DELETE_ENDPOINT = "/delete";
    String MAIL_ENDPOINT = "/email";
    String NOM_ENDPOINT = "/nom";
    String UPDATE_ENDPOINT = "/update";

    String COMMANDE_FOURNISSEUR_ENDPOINT = APP_ROOT + "/commande-fournisseur";
    String CREATE_COMMANDE_FOURNISSEUR = COMMANDE_FOURNISSEUR_ENDPOINT + CREATE_ENDPOINT + "/{dateCommandeFournisseur}";
    String UPDATE_COMMANDE_FOURNISSEUR_ENDPOINT = COMMANDE_FOURNISSEUR_ENDPOINT + UPDATE_ENDPOINT;
    String FIND_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT = COMMANDE_FOURNISSEUR_ENDPOINT + "/{idCommandeFournisseur}";
    String FIND_COMMANDE_FOURNISSEUR_BY_CODE_ENDPOINT = COMMANDE_FOURNISSEUR_ENDPOINT + CODE_ENDPOINT + "/{codeCommandeFournisseur}";
    String FIND_COMMANDE_FOURNISSEUR_BY_DATE_ENDPOINT = COMMANDE_FOURNISSEUR_ENDPOINT + DATE_ENDPOINT + "/{dateCommandeFournisseur}";
    String FIND_ALL_COMMANDE_FOURNISSEUR = COMMANDE_FOURNISSEUR_ENDPOINT + ALL_ENDPOINT;
    String DELETE_COMMANDE_FOURNISSEUR_BY_ID_ENDPOINT = COMMANDE_FOURNISSEUR_ENDPOINT + DELETE_ENDPOINT + "/{idCommandeFournisseur}";

    String ENTREPRISE_ENDPOINT = APP_ROOT + "/entreprise";
    String CREATE_ENTREPRISE_ENDPOINT = ENTREPRISE_ENDPOINT + CREATE_ENDPOINT;
    String FIND_ENTREPRISE_BY_ID_ENDPOINT = ENTREPRISE_ENDPOINT + "/{idEntreprise}";
    String FIND_ENTREPRISE_BY_NOM_ENDPOINT = ENTREPRISE_ENDPOINT + NOM_ENDPOINT + "{nomEntreprise}";
    String FIND_ENTREPRISE_BY_MAIL_ENDPOINT = ENTREPRISE_ENDPOINT + MAIL_ENDPOINT + "/{emailEntreprise}";
    String FIND_ALL_ENTREPRISE_ENDPOINT = ENTREPRISE_ENDPOINT + ALL_ENDPOINT;
    String DELETE_ENTREPRISE_BY_ID_ENDPOINT = ENTREPRISE_ENDPOINT + DELETE_ENDPOINT + "{idEntreprise}";
    String AUTHENTICATION_ENDPOINT = APP_ROOT + "/auth";

}
