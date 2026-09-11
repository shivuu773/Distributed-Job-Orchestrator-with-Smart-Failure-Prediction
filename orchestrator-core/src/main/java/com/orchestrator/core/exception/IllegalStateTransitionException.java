package com.orchestrator.core.exception;
// Thrown when a job attempts a state mutation not permitted by the state machine (e.g., modifying a terminal state).
public class IllegalStateTransitionException extends RuntimeException {

    private final String currentState;
    private final String attemptedState;

    public IllegalStateTransitionException(String currentState, String attemptedState) {
        super(String.format("Illegal state transition from [%s] to [%s]", currentState, attemptedState));
        this.currentState = currentState;
        this.attemptedState = attemptedState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getAttemptedState() {
        return attemptedState;
    }
}