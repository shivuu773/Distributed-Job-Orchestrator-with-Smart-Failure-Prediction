package com.orchestrator.core.strategy.retry;

import java.time.Duration;
// It abstracts two decisions: eligibility (shouldRetry) and timing (calculateNextBackoffMs). Duration and millisecond primitives avoid ambiguous integer units.

public interface RetryPolicy {

    /**
     * Determines whether an execution is eligible for another attempt.
     *
     * @param currentAttempt The number of attempts already executed (1-based index).
     * @param maxRetries     The ceiling of allowed retries.
     * @return true if an additional attempt is permitted, false otherwise.
     */
    boolean shouldRetry(int currentAttempt, int maxRetries);

    /**
     * Computes the wait duration before the next retry is eligible for execution.
     *
     * @param currentAttempt The current attempt count.
     * @return Duration representing the backoff delay.
     */
    Duration calculateNextBackoff(int currentAttempt);
}