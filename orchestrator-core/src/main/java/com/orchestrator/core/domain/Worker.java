/**
 * ============================================================================
 * ENTITY: Worker
 * ============================================================================
 * WHAT: Entity representing an active or dead worker node in the cluster.
 * WHY : Enables the orchestrator to track worker liveness via periodic heartbeats
 *       and identify dead nodes whose leases need to be reclaimed.
 * HOW : Persisted in MySQL, tracking WorkerStatus, lastHeartbeatAt, and registration time.
 * ============================================================================
 */
package com.orchestrator.core.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WorkerStatus status;

    @Column(name = "last_heartbeat_at", nullable = false)
    private Instant lastHeartbeatAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    protected Worker() {}

    public Worker(String id) {
        this.id = Objects.requireNonNull(id, "Worker id cannot be null");
        this.status = WorkerStatus.IDLE;
        Instant now = Instant.now();
        this.registeredAt = now;
        this.lastHeartbeatAt = now;
    }

    public void updateHeartbeat() {
        this.lastHeartbeatAt = Instant.now();
        if (this.status == WorkerStatus.DEAD) {
            this.status = WorkerStatus.IDLE;
        }
    }

    public void markBusy() {
        this.status = WorkerStatus.BUSY;
        this.lastHeartbeatAt = Instant.now();
    }

    public void markIdle() {
        this.status = WorkerStatus.IDLE;
        this.lastHeartbeatAt = Instant.now();
    }

    public void markDead() {
        this.status = WorkerStatus.DEAD;
    }

    public boolean isDead(long heartbeatTimeoutSeconds) {
        return Instant.now().isAfter(this.lastHeartbeatAt.plusSeconds(heartbeatTimeoutSeconds));
    }

    public String getId() { return id; }
    public WorkerStatus getStatus() { return status; }
    public Instant getLastHeartbeatAt() { return lastHeartbeatAt; }
    public Instant getRegisteredAt() { return registeredAt; }
}