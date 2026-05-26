package com.devtech.gestiondestock.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * @author luca
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("GESTION DE STOCK API Docs")
                        .description("GESTION DE STOCK REST API documentation")
                        .version("v1.0.0"));
    }

    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("01. Authentication")
                .pathsToMatch("/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryApi() {
        return GroupedOpenApi.builder()
                .group("02. Category")
                .pathsToMatch("/v1/category/**")
                .build();
    }

    @Bean
    public GroupedOpenApi clientApi() {
        return GroupedOpenApi.builder()
                .group("03. Client")
                .pathsToMatch("/v1/client/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fournisseurApi() {
        return GroupedOpenApi.builder()
                .group("04. Fournisseur")
                .pathsToMatch("/v1/fournisseur/**")
                .build();
    }

    @Bean
    public GroupedOpenApi articleApi() {
        return GroupedOpenApi.builder()
                .group("05. Article")
                .pathsToMatch("/v1/articles/**")
                .build();
    }

    @Bean
    public GroupedOpenApi entrepotApi() {
        return GroupedOpenApi.builder()
                .group("06. Entrepot")
                .pathsToMatch("/v1/entrepots/**")
                .build();
    }

    @Bean
    public GroupedOpenApi lotApi() {
        return GroupedOpenApi.builder()
                .group("07. Lot")
                .pathsToMatch("/v1/lots/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commandeClientApi() {
        return GroupedOpenApi.builder()
                .group("08. Commande Client")
                .pathsToMatch("/v1/commande-client/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commandeFournisseurApi() {
        return GroupedOpenApi.builder()
                .group("09. Commande Fournisseur")
                .pathsToMatch("/v1/commande-fournisseur/**")
                .build();
    }

    @Bean
    public GroupedOpenApi venteApi() {
        return GroupedOpenApi.builder()
                .group("10. Vente")
                .pathsToMatch("/v1/vente/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mvtStkApi() {
        return GroupedOpenApi.builder()
                .group("11. MvtStk")
                .pathsToMatch("/v1/mvtstk/**")
                .build();
    }

    @Bean
    public GroupedOpenApi factureApi() {
        return GroupedOpenApi.builder()
                .group("12. Facture")
                .pathsToMatch("/v1/factures/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paiementApi() {
        return GroupedOpenApi.builder()
                .group("13. Paiement")
                .pathsToMatch("/v1/paiements/**")
                .build();
    }

    @Bean
    public GroupedOpenApi transfertStockApi() {
        return GroupedOpenApi.builder()
                .group("14. Transfert Stock")
                .pathsToMatch("/v1/transferts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi inventaireApi() {
        return GroupedOpenApi.builder()
                .group("15. Inventaire")
                .pathsToMatch("/v1/inventaires/**")
                .build();
    }

    @Bean
    public GroupedOpenApi regleTarifaireApi() {
        return GroupedOpenApi.builder()
                .group("16. Regle Tarifaire")
                .pathsToMatch("/v1/regles-tarifaires/**")
                .build();
    }

    @Bean
    public GroupedOpenApi avoirApi() {
        return GroupedOpenApi.builder()
                .group("17. Avoir")
                .pathsToMatch("/v1/avoirs/**")
                .build();
    }

    @Bean
    public GroupedOpenApi utilisateurApi() {
        return GroupedOpenApi.builder()
                .group("18. Utilisateur")
                .pathsToMatch("/v1/utilisateur/**")
                .build();
    }

    @Bean
    public GroupedOpenApi entrepriseApi() {
        return GroupedOpenApi.builder()
                .group("19. Entreprise")
                .pathsToMatch("/v1/entreprise/**")
                .build();
    }

    @Bean
    public GroupedOpenApi photoApi() {
        return GroupedOpenApi.builder()
                .group("20. Photo")
                .pathsToMatch("/v1/photos/**", "/photos/**")
                .build();
    }
}
