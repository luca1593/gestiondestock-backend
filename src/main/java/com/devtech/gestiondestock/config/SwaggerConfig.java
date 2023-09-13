package com.devtech.gestiondestock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luca
 */
@Configuration
public class SwaggerConfig {

    License license = new License();

    @Bean
    public OpenAPI springShopOpenAPI() {
        this.license.setName("DETECH Licence");
        this.license.setIdentifier("devtech");
        this.license.setUrl("devtech.licence.mg");
        return new OpenAPI()
                .info(new Info().title("GESTION DE STOCK API Docs")
                        .description("Documentation pour l'API de Gestion de stock")
                        .license(this.license)
                        .version("v1.0.0"));
    }
}
