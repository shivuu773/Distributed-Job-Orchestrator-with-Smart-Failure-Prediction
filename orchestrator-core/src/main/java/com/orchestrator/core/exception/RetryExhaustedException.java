package com.orchestrator.core.exception;
// Thrown when an execution failure occurs but the job has reached its maximum configured retry limit.

public class RetryExhaustedException extends RuntimeException {

    private final String jobId;
    private final int attempts;

    public RetryExhaustedException(String jobId, int attempts) {
        super(String.format("Job [%s] has exhausted all retry attempts (total attempts: %d)", jobId, attempts));
        this.jobId = jobId;
        this.attempts = attempts;
    }

    public String getJobId() {
        return jobId;
    }

    public int getAttempts() {
        return attempts;
    }
}