package com.harvesttracker.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI harvestTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Harvest Tracker API")
                        .description("Scaffolded backend API for future development")
                        .version("0.1.0"));
    }
}
