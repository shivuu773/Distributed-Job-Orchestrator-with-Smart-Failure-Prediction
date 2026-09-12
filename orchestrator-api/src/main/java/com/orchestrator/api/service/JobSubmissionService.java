package com.orchestrator.api.service;


import com.orchestrator.api.dto.JobSubmissionRequest;
import com.orchestrator.api.dto.JobResponse;
import com.orchestrator.core.domain.Job;
import com.orchestrator.core.domain.JobState;
import com.orchestrator.api.repository.JobRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JobSubmissionService {

    private final JobRepository jobRepository;
    private final LocalRiskEvaluator riskEvaluator;

    // Constructor Injection
    public JobSubmissionService(JobRepository jobRepository, LocalRiskEvaluator riskEvaluator) {
        this.jobRepository = jobRepository;
        this.riskEvaluator = riskEvaluator;
    }

    @Transactional
    public JobResponse submitAndEvaluateJob(JobSubmissionRequest request) {
        
        // 1. Evaluate locally
        int riskScore = riskEvaluator.evaluateRisk(request.getType(), request.getPayload());
        long predictedDuration = riskEvaluator.estimateDuration(request.getType());

        // 2. Create Job Entity
        Job newJob = new Job();
        newJob.setId(UUID.randomUUID().toString());
        newJob.setType(request.getType());
        newJob.setPayload(request.getPayload());
        newJob.setState(JobState.PENDING);
        newJob.setRiskScore(riskScore);
        newJob.setPredictedDurationMs(predictedDuration);
        newJob.setCreatedAt(LocalDateTime.now());
        newJob.setUpdatedAt(LocalDateTime.now());

        // 3. Save to Database
        Job savedJob = jobRepository.save(newJob);

        // 4. Return Response
        return new JobResponse(
            savedJob.getId(),
            savedJob.getState().name(),
            savedJob.getType(),
            savedJob.getCreatedAt()
        );
    }
}