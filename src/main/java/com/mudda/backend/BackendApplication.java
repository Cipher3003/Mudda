package com.mudda.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@OpenAPIDefinition(info = @Info(
        title = "Mudda API",
        version = "0.1.0",
        description = "API docs for Mudda Backend"))
@SpringBootApplication()
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
