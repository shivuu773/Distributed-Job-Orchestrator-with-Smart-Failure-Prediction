package com.orchestrator.core.strategy.retry;
// Provides predictable, constant wait intervals for idempotent batch jobs or predictable internal delays. Implements validation guards in the constructor to prevent non-positive delay values.
import java.time.Duration;

public class FixedDelayRetryPolicy implements RetryPolicy {

    private final Duration delay;

    public FixedDelayRetryPolicy(Duration delay) {
        if (delay == null || delay.isNegative() || delay.isZero()) {
            throw new IllegalArgumentException("Delay must be strictly positive");
        }
        this.delay = delay;
    }

    @Override
    public boolean shouldRetry(int currentAttempt, int maxRetries) {
        return currentAttempt <= maxRetries;
    }

    @Override
    public Duration calculateNextBackoff(int currentAttempt) {
        return this.delay;
    }

    public Duration getDelay() {
        return delay;
    }
}