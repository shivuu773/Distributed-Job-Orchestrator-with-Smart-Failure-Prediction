package com.orchestrator.core.exception;
// Thrown when a worker attempts to claim a job that has already been acquired by another concurrent thread or worker.

public class JobClaimConflictException extends RuntimeException {

    private final String jobId;
    private final String workerId;

    public JobClaimConflictException(String jobId, String workerId) {
        super(String.format("Worker [%s] failed to claim job [%s]: Job is already claimed or no longer pending", workerId, jobId));
        this.jobId = jobId;
        this.workerId = workerId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getWorkerId() {
        return workerId;
    }
}