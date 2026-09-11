/**
 * ============================================================================
 * ENTITY: JobHistory (Audit Log)
 * ============================================================================
 * WHAT: Append-only ledger tracking all state transitions for every job.
 * WHY : Distributed systems require strict forensic auditability to diagnose
 *       transient failures, worker timeouts, and re-executions.
 * HOW : Automatically populated on state changes; immutable once written.
 * ============================================================================
 */
package com.orchestrator.core.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "job_history")
public class JobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_id", nullable = false, length = 36)
    private String jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_state", length = 30)
    private JobState fromState;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_state", nullable = false, length = 30)
    private JobState toState;

    @Column(name = "worker_id", length = 64)
    private String workerId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected JobHistory() {}

    public JobHistory(String jobId, JobState fromState, JobState toState, String workerId, String details) {
        this.jobId = Objects.requireNonNull(jobId, "Job ID cannot be null");
        this.fromState = fromState;
        this.toState = Objects.requireNonNull(toState, "Target state cannot be null");
        this.workerId = workerId;
        this.details = details;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getJobId() { return jobId; }
    public JobState getFromState() { return fromState; }
    public JobState getToState() { return toState; }
    public String getWorkerId() { return workerId; }
    public String getDetails() { return details; }
    public Instant getCreatedAt() { return createdAt; }
}