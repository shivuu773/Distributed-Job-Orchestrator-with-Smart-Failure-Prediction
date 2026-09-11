/**
 * ============================================================================
 * TEST SUITE: JobTest
 * ============================================================================
 * WHAT: Verifies the business operations and lifecycle transitions of the Job entity.
 * WHY : Ensures domain invariants on the aggregate root execute as intended.
 * HOW : Simulates lifecycle methods and asserts resulting state and retry counters.
 * ============================================================================
 */
package com.orchestrator.core.domain;

import com.orchestrator.core.exception.IllegalStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    @Test
    @DisplayName("New job initializes in PENDING state with zero retries")
    void testInitialState() {
        Job job = new Job(JobType.PAYMENT_PROCESSING, "{\"amount\": 100}", 5, 3);

        assertNotNull(job.getId());
        assertEquals(JobState.PENDING, job.getState());
        assertEquals(0, job.getCurrentRetries());
        assertEquals(3, job.getMaxRetries());
        assertEquals(5, job.getPriority());
        assertNull(job.getAssignedWorkerId());
    }

    @Test
    @DisplayName("Happy path lifecycle progresses to COMPLETED")
    void testHappyPathLifecycle() {
        Job job = new Job(JobType.EMAIL_NOTIFICATION, "{}", 1, 2);

        job.assignToWorker("worker-alpha");
        assertEquals(JobState.ASSIGNED, job.getState());
        assertEquals("worker-alpha", job.getAssignedWorkerId());

        job.startExecution();
        assertEquals(JobState.RUNNING, job.getState());

        job.recordSuccess();
        assertEquals(JobState.COMPLETED, job.getState());
    }

    @Test
    @DisplayName("Failing a job with retries increments count and reschedules to PENDING")
    void testFailureWithRetriesRemaining() {
        Job job = new Job(JobType.REPORT_GENERATION, "{}", 1, 3);
        job.assignToWorker("worker-1");
        job.startExecution();

        Instant nextRun = Instant.now().plusSeconds(60);
        job.recordFailure(true, nextRun);

        assertEquals(1, job.getCurrentRetries());
        assertEquals(JobState.PENDING, job.getState());
        assertNull(job.getAssignedWorkerId());
        assertEquals(nextRun, job.getScheduledAt());
    }

    @Test
    @DisplayName("Failing a job with exhausted retries transitions to DEAD_LETTER")
    void testFailureExhaustedRetries() {
        Job job = new Job(JobType.PAYMENT_PROCESSING, "{}", 1, 1);
        job.assignToWorker("worker-1");
        job.startExecution();

        job.recordFailure(false, null);

        assertEquals(1, job.getCurrentRetries());
        assertEquals(JobState.DEAD_LETTER, job.getState());
    }

    @Test
    @DisplayName("Invalid state jump on Job entity throws IllegalStateTransitionException")
    void testInvalidTransitionThrows() {
        Job job = new Job(JobType.EMAIL_NOTIFICATION, "{}", 1, 2);

        assertThrows(IllegalStateTransitionException.class, job::recordSuccess);
    }
}