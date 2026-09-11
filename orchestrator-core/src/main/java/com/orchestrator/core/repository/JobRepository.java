/**
 * ============================================================================
 * REPOSITORY: JobRepository
 * ============================================================================
 * WHAT: Persistence operations for the Job aggregate root.
 * WHY : Implements race-condition-free job claiming via MySQL 8.0's 
 *       SELECT ... FOR UPDATE SKIP LOCKED, preventing concurrent worker collisions.
 * HOW : Spring Data JPA interface backed by native locking SQL.
 * ============================================================================
 */
package com.orchestrator.core.repository;

import com.orchestrator.core.domain.Job;
import com.orchestrator.core.domain.JobState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    List<Job> findByState(JobState state);

    List<Job> findByAssignedWorkerId(String assignedWorkerId);

    /**
     * Atomically selects and locks pending jobs that are eligible for execution.
     * Uses SKIP LOCKED so competing worker threads/nodes do not block each other
     * or double-claim identical jobs.
     */
    @Query(value = """
            SELECT * FROM jobs j
            WHERE j.state = 'PENDING'
              AND j.scheduled_at <= :now
            ORDER BY j.priority DESC, j.scheduled_at ASC
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<Job> claimPendingJobsWithSkipLocked(@Param("now") Instant now, @Param("batchSize") int batchSize);
}