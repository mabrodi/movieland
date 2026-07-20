package org.dimchik.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.dimchik.security.PublicEndpoint;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("1.0.0")
                        .title("MovieLand")
                        .description("REST API for managing movie catalog."))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            boolean isPublic = handlerMethod.hasMethodAnnotation(PublicEndpoint.class);

            operation.getResponses()
                    .addApiResponse("500", new ApiResponse().description("Server error"));

            if (!isPublic) {
                operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                operation.getResponses()
                        .addApiResponse("401", new ApiResponse().description("No auth"));

                if (handlerMethod.hasMethodAnnotation(PreAuthorize.class)) {
                    operation.getResponses()
                            .addApiResponse("403", new ApiResponse().description("Access denied"));
                }
            }

            return operation;
        };
    }
}
