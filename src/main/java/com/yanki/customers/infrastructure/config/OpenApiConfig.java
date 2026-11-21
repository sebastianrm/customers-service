package com.yanki.customers.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion OpenAPI del microservicio de clientes.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Construye la definicion OpenAPI de la plataforma Yanki.
     *
     * @return definicion OpenAPI del microservicio
     */
    @Bean
    public OpenAPI customersOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Yanki Customers API")
                .description("Maestro de clientes de la plataforma bancaria Yanki")
                .version("1.0.0")
                .contact(new Contact().name("Yanki Platform"))
                .license(new License().name("MIT")));
    }
}
