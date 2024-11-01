package br.com.contafacil.bonnarotec.storage.config;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenAPIConfig {

    @Value("${gateway_url}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Storage API")
                        .version("1.0")
                        .description("Serviço responsável pelo armazenamento de arquivos")
                        .contact(new Contact()
                                .name("ContaFacil")
                                .email("contato@contafacil.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .servers(Collections.singletonList(
                        new Server()
                                .url(gatewayUrl + "/cfstorage")
                                .description("Gateway URL")
                ));
    }
}
