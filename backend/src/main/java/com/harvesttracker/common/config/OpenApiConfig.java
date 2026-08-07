package com.harvesttracker.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI harvestTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harvest Tracker REST API")
                        .description("Backend REST API for Harvest Tracker — Multi-Fruit Harvest Management Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Harvest Tracker Engineering")
                                .email("engineering@harvesttracker.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://harvesttracker.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ));
    }
}
