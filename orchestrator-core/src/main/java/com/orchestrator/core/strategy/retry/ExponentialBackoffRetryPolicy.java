// Uses the standard enterprise formula:$$\text{Interval} = \min\left(\text{maxBackoff}, \text{initialBackoff} \times 2^{\text{attempt} - 1}\right)$$When jitter is enabled, it applies "Full Jitter":$$\text{ActualDelay} = \text{randomUniform}(0, \text{Interval})$$This decorrelates retrying clients, preventing synchronized waves from repeatedly crashing downstream resources. It accepts a java.util.Random instance in its secondary constructor to allow deterministic seeding during unit tests.


package com.orchestrator.core.strategy.retry;

import java.time.Duration;
import java.util.Random;

public class ExponentialBackoffRetryPolicy implements RetryPolicy {

    private final Duration initialBackoff;
    private final Duration maxBackoff;
    private final double multiplier;
    private final boolean useJitter;
    private final Random random;

    public ExponentialBackoffRetryPolicy(Duration initialBackoff, Duration maxBackoff, double multiplier, boolean useJitter) {
        this(initialBackoff, maxBackoff, multiplier, useJitter, new Random());
    }

    // Constructor with injected Random for deterministic unit testing
    public ExponentialBackoffRetryPolicy(Duration initialBackoff, Duration maxBackoff, double multiplier, boolean useJitter, Random random) {
        if (initialBackoff == null || initialBackoff.isNegative() || initialBackoff.isZero()) {
            throw new IllegalArgumentException("Initial backoff must be strictly positive");
        }
        if (maxBackoff == null || maxBackoff.compareTo(initialBackoff) < 0) {
            throw new IllegalArgumentException("Max backoff cannot be less than initial backoff");
        }
        if (multiplier < 1.0) {
            throw new IllegalArgumentException("Multiplier must be >= 1.0");
        }
        this.initialBackoff = initialBackoff;
        this.maxBackoff = maxBackoff;
        this.multiplier = multiplier;
        this.useJitter = useJitter;
        this.random = random != null ? random : new Random();
    }

    @Override
    public boolean shouldRetry(int currentAttempt, int maxRetries) {
        return currentAttempt <= maxRetries;
    }

    @Override
    public Duration calculateNextBackoff(int currentAttempt) {
        int attempt = Math.max(1, currentAttempt);
        
        // Calculate: initialBackoff * multiplier^(attempt - 1)
        double calculatedDelayMs = initialBackoff.toMillis() * Math.pow(multiplier, attempt - 1);
        long cappedDelayMs = Math.min((long) calculatedDelayMs, maxBackoff.toMillis());

        if (!useJitter) {
            return Duration.ofMillis(cappedDelayMs);
        }

        // Full Jitter: Uniform random between 0 and cappedDelayMs
        long jitteredDelayMs = (long) (random.nextDouble() * cappedDelayMs);
        return Duration.ofMillis(jitteredDelayMs);
    }
}