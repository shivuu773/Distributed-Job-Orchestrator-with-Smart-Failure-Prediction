package com.orchestrator.core.domain;

import com.orchestrator.core.exception.IllegalStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class JobStateTest {

    @Test
    @DisplayName("PENDING can only transition to ASSIGNED")
    void testPendingTransitions() {
        assertTrue(JobState.PENDING.canTransitionTo(JobState.ASSIGNED));
        assertFalse(JobState.PENDING.canTransitionTo(JobState.RUNNING));
        assertFalse(JobState.PENDING.canTransitionTo(JobState.COMPLETED));
        assertFalse(JobState.PENDING.canTransitionTo(JobState.FAILED));
        assertFalse(JobState.PENDING.canTransitionTo(null));
    }

    @Test
    @DisplayName("ASSIGNED can transition to RUNNING or roll back to PENDING")
    void testAssignedTransitions() {
        assertTrue(JobState.ASSIGNED.canTransitionTo(JobState.RUNNING));
        assertTrue(JobState.ASSIGNED.canTransitionTo(JobState.PENDING));
        assertFalse(JobState.ASSIGNED.canTransitionTo(JobState.COMPLETED));
    }

    @Test
    @DisplayName("RUNNING can transition to COMPLETED, FAILED, or RETRYING")
    void testRunningTransitions() {
        assertTrue(JobState.RUNNING.canTransitionTo(JobState.COMPLETED));
        assertTrue(JobState.RUNNING.canTransitionTo(JobState.FAILED));
        assertTrue(JobState.RUNNING.canTransitionTo(JobState.RETRYING));
        assertFalse(JobState.RUNNING.canTransitionTo(JobState.PENDING));
    }

    @Test
    @DisplayName("RETRYING can only transition back to PENDING for re-queueing")
    void testRetryingTransitions() {
        assertTrue(JobState.RETRYING.canTransitionTo(JobState.PENDING));
        assertFalse(JobState.RETRYING.canTransitionTo(JobState.RUNNING));
        assertFalse(JobState.RETRYING.canTransitionTo(JobState.COMPLETED));
    }

    @Test
    @DisplayName("FAILED can transition to DEAD_LETTER or RETRYING")
    void testFailedTransitions() {
        assertTrue(JobState.FAILED.canTransitionTo(JobState.DEAD_LETTER));
        assertTrue(JobState.FAILED.canTransitionTo(JobState.RETRYING));
        assertFalse(JobState.FAILED.canTransitionTo(JobState.RUNNING));
    }

    @ParameterizedTest
    @EnumSource(JobState.class)
    @DisplayName("COMPLETED is terminal and rejects transitions to all states")
    void testCompletedIsTerminal(JobState target) {
        assertTrue(JobState.COMPLETED.isTerminal());
        assertFalse(JobState.COMPLETED.canTransitionTo(target));
        assertThrows(IllegalStateTransitionException.class, () -> JobState.COMPLETED.validateTransition(target));
    }

    @ParameterizedTest
    @EnumSource(JobState.class)
    @DisplayName("DEAD_LETTER is terminal and rejects transitions to all states")
    void testDeadLetterIsTerminal(JobState target) {
        assertTrue(JobState.DEAD_LETTER.isTerminal());
        assertFalse(JobState.DEAD_LETTER.canTransitionTo(target));
        assertThrows(IllegalStateTransitionException.class, () -> JobState.DEAD_LETTER.validateTransition(target));
    }

    @Test
    @DisplayName("validateTransition throws IllegalStateTransitionException with state details")
    void testValidateTransitionExceptionDetails() {
        IllegalStateTransitionException ex = assertThrows(
                IllegalStateTransitionException.class,
                () -> JobState.PENDING.validateTransition(JobState.COMPLETED)
        );

        assertEquals("PENDING", ex.getCurrentState());
        assertEquals("COMPLETED", ex.getAttemptedState());
        assertTrue(ex.getMessage().contains("Illegal state transition from [PENDING] to [COMPLETED]"));
    }
}