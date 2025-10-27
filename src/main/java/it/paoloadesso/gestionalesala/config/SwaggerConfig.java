package it.paoloadesso.gestionalesala.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gestionale Sala - Sistema Cassa",
                description = "API di cassa: pagamenti, chiusure, storico, amministrazione",
                version = "1.0.0"
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = """
                SISTEMA CASSA - Solo personale autorizzato:
                
                GESTORE/CASSIERE:
                • gestore/gestore
                • cassiere/cassiere
                • admin/admin
                
                Accesso completo a tutte le operazioni finanziarie e amministrative
                """
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Gestionale Sala - Sistema Cassa")
                        .description("API per la gestione della cassa del ristorante - Solo personale autorizzato")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Paolo Adesso")
                                .email("paoloadesso@outlook.it")));
    }
}
