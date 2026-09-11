package com.orchestrator.api.service;

import com.orchestrator.api.dto.JobSubmissionRequest;
import com.orchestrator.api.dto.JobResponse;
import com.orchestrator.core.domain.Job;
import com.orchestrator.core.domain.JobState;
import com.orchestrator.api.repository.JobRepository;
import com.orchestrator.api.ai.GeminiRiskPredictor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JobSubmissionService {

    private final JobRepository jobRepository;
    private final GeminiRiskPredictor aiPredictor;

    public JobSubmissionService(JobRepository jobRepository, GeminiRiskPredictor aiPredictor) {
        this.jobRepository = jobRepository;
        this.aiPredictor = aiPredictor;
    }

    @Transactional
    public JobResponse submitAndEvaluateJob(JobSubmissionRequest request) {
        
        int riskScore = 1; 
        long predictedDuration = 1000L; 

        try {
            riskScore = aiPredictor.predictRisk(request.getType(), request.getPayload());
            predictedDuration = aiPredictor.predictDuration(request.getType());
        } catch (Exception e) {
            System.err.println("AI API unavailable. Fallback applied. Reason: " + e.getMessage());
            riskScore = calculateFallbackRisk(request.getType());
        }

        Job newJob = new Job();
        newJob.setId(UUID.randomUUID().toString());
        newJob.setType(request.getType());
        newJob.setPayload(request.getPayload());
        newJob.setState(JobState.PENDING);
        newJob.setRiskScore(riskScore);
        newJob.setPredictedDurationMs(predictedDuration);
        newJob.setCreatedAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(newJob);

        return new JobResponse(
            savedJob.getId(),
            savedJob.getState().name(),
            savedJob.getType(),
            savedJob.getCreatedAt()
        );
    }

    private int calculateFallbackRisk(String jobType) {
        if ("PAYMENT_PROCESSING".equalsIgnoreCase(jobType)) return 8; 
        if ("EMAIL_NOTIFICATION".equalsIgnoreCase(jobType)) return 2; 
        return 5; 
    }
}