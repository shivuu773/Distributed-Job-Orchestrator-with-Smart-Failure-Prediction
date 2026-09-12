package com.orchestrator.api.dto;

import java.time.LocalDateTime;

public class JobResponse {
    
    private String jobId;
    private String state;
    private String type;
    private LocalDateTime createdAt;

    public JobResponse(String jobId, String state, String type, LocalDateTime createdAt) {
        this.jobId = jobId;
        this.state = state;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Getters
    public String getJobId() { return jobId; }
    public String getState() { return state; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}