package com.orchestrator.api.repository;

import com.orchestrator.core.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    @Query(value = "SELECT * FROM jobs WHERE state = 'PENDING' ORDER BY risk_score ASC, created_at ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<Job> findAndLockNextPendingJob();
}