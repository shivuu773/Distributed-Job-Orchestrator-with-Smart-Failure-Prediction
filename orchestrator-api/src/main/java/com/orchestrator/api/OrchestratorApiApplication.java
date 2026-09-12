package com.orchestrator.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.orchestrator")
@EntityScan(basePackages = "com.orchestrator.core.domain")
@EnableJpaRepositories(basePackages = "com.orchestrator.core.repository")
public class OrchestratorApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApiApplication.class, args);
    }
}