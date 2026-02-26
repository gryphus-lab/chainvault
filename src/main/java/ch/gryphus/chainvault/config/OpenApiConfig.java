package ch.gryphus.chainvault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chainVaultOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChainVault API")
                        .version("0.0.1")
                        .description("APIs for ChainVault orchestration and migration service")
                        .contact(new Contact().name("gryphus-lab").email("noreply@gryphus-lab")));
    }
}
