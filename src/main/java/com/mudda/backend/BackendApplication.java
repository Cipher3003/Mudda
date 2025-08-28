package com.mudda.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Mudda API", version = "0.1.0", description = "API docs for Mudda Backend"))
@EnableJpaRepositories(basePackages = {
		"com.mudda.backend.postgres.repositories",
		"com.mudda.backend.amazon.repositories"
}) // Specify your repository package
@SpringBootApplication()
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
