package com.orchestrator.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- Yeh import karna hai

@SpringBootApplication
@EnableScheduling // <-- Yeh annotation yahan lagana hai
public class OrchestratorWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorWorkerApplication.class, args);
    }
}