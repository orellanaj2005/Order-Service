package com.smartlogix.pedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OpenApiConfig – cobertura del bean pedidosOpenAPI")
class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    @DisplayName("pedidosOpenAPI – retorna una instancia OpenAPI no nula")
    void pedidosOpenAPI_retornaInstanciaNoNula() {
        OpenAPI api = config.pedidosOpenAPI();
        assertThat(api).isNotNull();
    }

    @Test
    @DisplayName("pedidosOpenAPI – el título corresponde al servicio de Pedidos")
    void pedidosOpenAPI_titulo_correcto() {
        OpenAPI api = config.pedidosOpenAPI();
        assertThat(api.getInfo().getTitle()).contains("Pedidos");
    }

    @Test
    @DisplayName("pedidosOpenAPI – incluye el esquema de seguridad bearerAuth")
    void pedidosOpenAPI_incluyeSecurityScheme() {
        OpenAPI api = config.pedidosOpenAPI();
        assertThat(api.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }

    @Test
    @DisplayName("pedidosOpenAPI – tiene al menos un security requirement")
    void pedidosOpenAPI_tieneSecurityRequirement() {
        OpenAPI api = config.pedidosOpenAPI();
        assertThat(api.getSecurity()).isNotEmpty();
    }
}
