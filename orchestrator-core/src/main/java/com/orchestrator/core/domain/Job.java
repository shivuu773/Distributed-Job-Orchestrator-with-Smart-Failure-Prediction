/**
 * ============================================================================
 * AGGREGATE ROOT ENTITY: Job
 * ============================================================================
 * WHAT: Core persistence entity representing an orchestrator unit of work.
 * WHY : Encapsulates state transitions, retries, and worker leases to enforce
 *       domain invariants directly inside the entity.
 * HOW : Uses JPA annotations for MySQL mapping, while routing state changes
 *       through JobState's transition validation matrix.
 * ============================================================================
 */
package com.orchestrator.core.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 50)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 30)
    private JobState state;

    @Lob
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "current_retries", nullable = false)
    private int currentRetries;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "predicted_risk_score")
    private Double predictedRiskScore;

    @Column(name = "predicted_duration_ms")
    private Long predictedDurationMs;

    @Column(name = "assigned_worker_id", length = 64)
    private String assignedWorkerId;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // JPA mandatory default constructor
    protected Job() {}

    public Job(JobType jobType, String payload, int priority, int maxRetries) {
        this.id = UUID.randomUUID().toString();
        this.jobType = Objects.requireNonNull(jobType, "JobType cannot be null");
        this.payload = payload;
        this.priority = priority;
        this.maxRetries = Math.max(0, maxRetries);
        this.currentRetries = 0;
        this.state = JobState.PENDING;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.scheduledAt = now;
    }

    // --- Domain Operations (Business Methods) ---

    public void transitionTo(JobState nextState) {
        this.state.validateTransition(nextState);
        this.state = nextState;
        this.updatedAt = Instant.now();
    }

    public void assignToWorker(String workerId) {
        transitionTo(JobState.ASSIGNED);
        this.assignedWorkerId = Objects.requireNonNull(workerId, "Worker ID cannot be null");
    }

    public void startExecution() {
        transitionTo(JobState.RUNNING);
    }

    public void recordSuccess() {
        transitionTo(JobState.COMPLETED);
    }

    public void recordFailure(boolean canRetry, Instant nextRetryAt) {
        this.currentRetries++;
        this.updatedAt = Instant.now();
        if (canRetry) {
            transitionTo(JobState.RETRYING);
            this.scheduledAt = nextRetryAt;
            this.assignedWorkerId = null;
            // Immediate transition back to PENDING so it can be claimed when scheduledAt arrives
            transitionTo(JobState.PENDING);
        } else {
            transitionTo(JobState.FAILED);
            transitionTo(JobState.DEAD_LETTER);
        }
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public JobType getJobType() { return jobType; }
    public JobState getState() { return state; }
    public String getPayload() { return payload; }
    public int getPriority() { return priority; }
    public int getCurrentRetries() { return currentRetries; }
    public int getMaxRetries() { return maxRetries; }
    public Double getPredictedRiskScore() { return predictedRiskScore; }
    public void setPredictedRiskScore(Double predictedRiskScore) { this.predictedRiskScore = predictedRiskScore; }
    public Long getPredictedDurationMs() { return predictedDurationMs; }
    public void setPredictedDurationMs(Long predictedDurationMs) { this.predictedDurationMs = predictedDurationMs; }
    public String getAssignedWorkerId() { return assignedWorkerId; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}