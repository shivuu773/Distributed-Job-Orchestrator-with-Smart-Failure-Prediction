package com.orchestrator.api.service;

import com.orchestrator.api.dto.JobSubmissionRequest;
import com.orchestrator.api.dto.JobResponse;
import com.orchestrator.core.domain.Job;
import com.orchestrator.core.domain.JobType;
import com.orchestrator.core.repository.JobRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class JobSubmissionService {

    private final JobRepository jobRepository;
    private final LocalRiskEvaluator riskEvaluator;

    public JobSubmissionService(JobRepository jobRepository, LocalRiskEvaluator riskEvaluator) {
        this.jobRepository = jobRepository;
        this.riskEvaluator = riskEvaluator;
    }

    @Transactional
    public JobResponse submitAndEvaluateJob(JobSubmissionRequest request) {
        // 1. Evaluate locally
        int riskScore = riskEvaluator.evaluateRisk(request.getType(), request.getPayload());
        long predictedDuration = riskEvaluator.estimateDuration(request.getType());

        // 2. Map string to JobType enum
        JobType jobType;
        try {
            jobType = JobType.valueOf(request.getType().toUpperCase());
        } catch (Exception e) {
            jobType = JobType.REPORT_GENERATION;
        }

        // 3. Create Job using the domain aggregate constructor
        Job newJob = new Job(jobType, request.getPayload(), 0, 3);
        newJob.setPredictedRiskScore((double) riskScore);
        newJob.setPredictedDurationMs(predictedDuration);

        // 4. Save to Database
        Job savedJob = jobRepository.save(newJob);

        // 5. Convert Instant to LocalDateTime for JobResponse DTO
        LocalDateTime createdAt = LocalDateTime.ofInstant(savedJob.getCreatedAt(), ZoneId.systemDefault());

        return new JobResponse(
            savedJob.getId(),
            savedJob.getState().name(),
            savedJob.getJobType().name(),
            createdAt
        );
    }
}