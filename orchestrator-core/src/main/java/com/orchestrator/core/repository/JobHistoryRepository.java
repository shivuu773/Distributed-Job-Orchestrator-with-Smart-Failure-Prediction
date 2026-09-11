/**
 * ============================================================================
 * REPOSITORY: JobHistoryRepository
 * ============================================================================
 * WHAT: Persistence interface for append-only state transition audit logs.
 * WHY : Supplies historical trail for debugging and telemetry.
 * HOW : JPA repository querying logs by jobId ordered chronologically.
 * ============================================================================
 */
package com.orchestrator.core.repository;

import com.orchestrator.core.domain.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {

    List<JobHistory> findByJobIdOrderByCreatedAtAsc(String jobId);
}