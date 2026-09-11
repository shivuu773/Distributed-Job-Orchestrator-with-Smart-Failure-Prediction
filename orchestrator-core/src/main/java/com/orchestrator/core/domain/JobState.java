package com.orchestrator.core.domain;

import com.orchestrator.core.exception.IllegalStateTransitionException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum JobState {

    PENDING,
    ASSIGNED,
    RUNNING,
    COMPLETED,
    FAILED,
    RETRYING,
    DEAD_LETTER;

    private Set<JobState> nextValidStates;

    static {
        PENDING.nextValidStates = EnumSet.of(ASSIGNED);
        ASSIGNED.nextValidStates = EnumSet.of(RUNNING, PENDING);
        RUNNING.nextValidStates = EnumSet.of(COMPLETED, FAILED, RETRYING);
        RETRYING.nextValidStates = EnumSet.of(PENDING);
        FAILED.nextValidStates = EnumSet.of(DEAD_LETTER, RETRYING);
        COMPLETED.nextValidStates = Collections.emptySet();
        DEAD_LETTER.nextValidStates = Collections.emptySet();
    }

    public boolean canTransitionTo(JobState targetState) {
        if (targetState == null) {
            return false;
        }
        return this.nextValidStates.contains(targetState);
    }

    public void validateTransition(JobState targetState) {
        if (!canTransitionTo(targetState)) {
            throw new IllegalStateTransitionException(
                    this.name(),
                    targetState != null ? targetState.name() : "NULL"
            );
        }
    }

    public boolean isTerminal() {
        return this.nextValidStates.isEmpty();
    }
}