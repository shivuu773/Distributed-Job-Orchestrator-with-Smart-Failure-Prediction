/**
 * ============================================================================
 * REPOSITORY: WorkerRepository
 * ============================================================================
 * WHAT: Persistence operations for worker cluster nodes.
 * WHY : Allows worker heartbeat tracking and stale worker identification.
 * HOW : Standard Spring Data JPA queries filtering on status and heartbeat cutoff.
 * ============================================================================
 */
package com.orchestrator.core.repository;

import com.orchestrator.core.domain.Worker;
import com.orchestrator.core.domain.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, String> {

    List<Worker> findByStatus(WorkerStatus status);

    @Query("SELECT w FROM Worker w WHERE w.status != 'DEAD' AND w.lastHeartbeatAt < :threshold")
    List<Worker> findStaleWorkers(@Param("threshold") Instant threshold);
}