package com.orchestrator.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.api.dto.JobResponse;
import com.orchestrator.api.dto.JobSubmissionRequest;
import com.orchestrator.api.service.JobSubmissionService;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobSubmissionService jobSubmissionService;

    // Constructor Injection
    public JobController(JobSubmissionService jobSubmissionService) {
        this.jobSubmissionService = jobSubmissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<JobResponse> submitJob(@RequestBody JobSubmissionRequest request) {
        JobResponse response = jobSubmissionService.submitAndEvaluateJob(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}