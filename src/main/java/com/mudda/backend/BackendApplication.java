package com.mudda.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import io.github.cdimascio.dotenv.Dotenv;

@OpenAPIDefinition(info = @Info(
        title = "Mudda API",
        version = "0.1.0",
        description = "API docs for Mudda Backend"))
@SpringBootApplication()
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BackendApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .directory(".") // project root
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // Set all environment variables from .env as system properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue().trim());
        });
        
        SpringApplication.run(BackendApplication.class, args);
    }
}
