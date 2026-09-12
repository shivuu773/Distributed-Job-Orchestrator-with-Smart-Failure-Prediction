package com.orchestrator.api.dto;

public class JobSubmissionRequest {
    
    private String type;
    private String payload;

    // Default constructor
    public JobSubmissionRequest() {}

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}