package com.orchestrator.worker.heartbeat;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartbeatEmitter {

    private final String workerId = "worker-" + System.currentTimeMillis();

    // Runs every 5 seconds (5000 milliseconds)
    @Scheduled(fixedRate = 5000)
    public void sendHeartbeat() {
        try {
            System.out.println("Sending heartbeat from Worker ID: " + workerId + " to Orchestrator...");
            
            // TODO: Add RestTemplate or WebClient call to hit Orchestrator API
            // Example: restTemplate.postForObject("http://localhost:8080/api/orchestrator/heartbeat", request, Void.class);
            
        } catch (Exception e) {
            System.err.println("Failed to send heartbeat: " + e.getMessage());
        }
    }
}