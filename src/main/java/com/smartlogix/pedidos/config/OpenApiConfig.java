package com.smartlogix.pedidos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración central de OpenAPI/Swagger para el microservicio de Pedidos.
 *
 * <p>Este servicio no valida JWT por sí mismo (lo hace el Api-Gateway), pero el
 * esquema de seguridad "bearerAuth" se documenta igualmente para que, al
 * consumir la API a través del gateway, Swagger UI envíe el token. Así la
 * documentación refleja el contrato real de acceso.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI pedidosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartLogix · API de Pedidos")
                        .description("Registro de pedidos, gestión de su estado (catálogo ESTADO) y métricas de pedidos.")
                        .version("v1")
                        .contact(new Contact().name("Equipo SmartLogix").email("ja.orellanap@duocuc.cl"))
                        .license(new License().name("Uso académico - DuocUC")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT validado por el Api-Gateway. Formato: Bearer <token>")));
    }
}
