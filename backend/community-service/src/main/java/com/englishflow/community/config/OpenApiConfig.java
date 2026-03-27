package com.englishflow.community.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI communityServiceAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("EnglishFlow Team");
        contact.setEmail("support@englishflow.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Community Service API")
                .version("1.0.0")
                .description("API for managing forum categories, topics, posts, and reactions in EnglishFlow")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
