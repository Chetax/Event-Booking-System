package com.meetmint.meetmint_backend.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean

    public OpenAPI openAPI() {
        String schemeName = "bearerScheme";
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement().addList(schemeName)
                )
                .components(
                        new Components().addSecuritySchemes(
                                schemeName
                                , new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")
                        )
                )
                .info(getInfo());
    }
    private Info getInfo() {
        Contact contact = new Contact();
        contact.setEmail("ndmorkar@gmail.com");
        contact.setName("Nilesh Morkar");
        contact.setUrl("https://www.google.com/");
        return new Info()
                .title("Hospital Management Backend")
                .description("This Is Hospital Management Backend Project API Developed By Nilesh Morkar")
                .contact(contact)
                .version("v0.0.1")
                .summary("Welcome !!!!!");
    }
}
