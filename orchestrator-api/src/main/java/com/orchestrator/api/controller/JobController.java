package com.orchestrator.api.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.api.dto.JobResponse;
import com.orchestrator.api.dto.JobSubmissionRequest;
import com.orchestrator.api.service.JobSubmissionService;
import com.orchestrator.core.domain.Job;
import com.orchestrator.core.repository.JobRepository;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobSubmissionService jobSubmissionService;
    private final JobRepository jobRepository;

    public JobController(JobSubmissionService jobSubmissionService, JobRepository jobRepository) {
        this.jobSubmissionService = jobSubmissionService;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<JobResponse> submitJob(@RequestBody JobSubmissionRequest request) {
        JobResponse response = jobSubmissionService.submitAndEvaluateJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }
}
