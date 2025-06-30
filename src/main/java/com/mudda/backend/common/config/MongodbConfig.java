package com.mudda.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//TODO
// fix MongoDb Config, although this file itself isn't needed
// do change it with corrected MongoTemplate
@Configuration
@EnableJpaRepositories(
        basePackages = "com.mudda.backend.mongodb.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class MongodbConfig {
    // Define DataSource, EntityManagerFactory, TransactionManager
}

