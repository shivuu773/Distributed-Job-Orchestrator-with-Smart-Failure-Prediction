-- ============================================================================
-- FLYWAY MIGRATION: V1__init_schema.sql
-- TARGET: MySQL 8.0+
-- WHAT  : Initial schema for orchestrator jobs, workers, and audit history.
-- WHY   : Provides strictly typed relational persistence and composite indexes
--         designed for high-throughput locking with SKIP LOCKED.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Table: workers
-- Tracks worker node registrations, states, and heartbeats.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS workers (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    last_heartbeat_at TIMESTAMP(6) NOT NULL,
    registered_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX idx_workers_status_heartbeat (status, last_heartbeat_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. Table: jobs
-- Core aggregate root table for units of work in the orchestrator.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jobs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    job_type VARCHAR(50) NOT NULL,
    state VARCHAR(30) NOT NULL,
    payload LONGTEXT NULL,
    priority INT NOT NULL DEFAULT 0,
    current_retries INT NOT NULL DEFAULT 0,
    max_retries INT NOT NULL DEFAULT 3,
    predicted_risk_score DOUBLE NULL,
    predicted_duration_ms BIGINT NULL,
    assigned_worker_id VARCHAR(64) NULL,
    scheduled_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    CONSTRAINT fk_jobs_assigned_worker 
        FOREIGN KEY (assigned_worker_id) 
        REFERENCES workers(id) 
        ON DELETE SET NULL,

    -- Composite index to support SKIP LOCKED job claiming:
    -- Query: WHERE state = 'PENDING' AND scheduled_at <= :now ORDER BY priority DESC, scheduled_at ASC
    INDEX idx_jobs_claim_pipeline (state, scheduled_at, priority),
    INDEX idx_jobs_worker (assigned_worker_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. Table: job_history
-- Append-only audit log of state machine transitions.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id VARCHAR(36) NOT NULL,
    from_state VARCHAR(30) NULL,
    to_state VARCHAR(30) NOT NULL,
    worker_id VARCHAR(64) NULL,
    details TEXT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_history_job 
        FOREIGN KEY (job_id) 
        REFERENCES jobs(id) 
        ON DELETE CASCADE,

    INDEX idx_history_job_timeline (job_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;