/**
 * ============================================================================
 * TEST SUITE: RetryPolicyTest
 * ============================================================================
 * 
 * WHAT IS THIS FILE?
 * - A unit test suite verifying the behavior, math, and validation rules of our 
 *   retry policies (FixedDelayRetryPolicy & ExponentialBackoffRetryPolicy).
 * 
 * WHY DO WE NEED IT?
 * 1. Fast, Deterministic Math Checks:
 *    - In distributed systems, retry intervals dictate how traffic bounces back 
 *      after an outage. We must verify exponential backoff calculations and 
 *      capping limits mathematically without using slow Thread.sleep() calls.
 * 2. Thundering Herd Jitter Validation:
 *    - Full jitter relies on randomness to spread out retries. By injecting a 
 *      deterministic mock Random instance, we verify that the jitter calculation 
 *      stays strictly within the calculated interval [0, DelayMs] every time.
 * 3. Invariant Safety:
 *    - Confirms that invalid parameters (zero delay, negative duration, 
 *      multiplier < 1.0) fail immediately at initialization with IllegalArgumentException.
 * 
 * HOW DOES IT WORK?
 * - testFixedDelayPolicy:
 *   Asserts that FixedDelayRetryPolicy always returns the exact same delay 
 *   regardless of attempt count, and stops permitting retries when attempt > maxRetries.
 * - testExponentialBackoffWithoutJitter:
 *   Validates that the formula (initialBackoff * multiplier^(attempt - 1)) doubles 
 *   as expected (100ms -> 200ms -> 400ms -> 800ms) and strictly caps at 1000ms.
 * - testExponentialBackoffWithJitter:
 *   Uses a controlled pseudo-random seed returning 0.5 to assert exact halved delay 
 *   and verify jitter dispersion logic.
 * - test...Validation:
 *   Asserts that boundary edge-cases throw IllegalArgumentException.
 * ============================================================================
 */


package com.orchestrator.core.strategy.retry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    @Test
    @DisplayName("FixedDelayRetryPolicy should always return exact fixed delay")
    void testFixedDelayPolicy() {
        FixedDelayRetryPolicy policy = new FixedDelayRetryPolicy(Duration.ofSeconds(2));

        assertEquals(Duration.ofSeconds(2), policy.calculateNextBackoff(1));
        assertEquals(Duration.ofSeconds(2), policy.calculateNextBackoff(5));

        assertTrue(policy.shouldRetry(1, 3));
        assertTrue(policy.shouldRetry(3, 3));
        assertFalse(policy.shouldRetry(4, 3));
    }

    @Test
    @DisplayName("FixedDelayRetryPolicy should reject zero or negative delays")
    void testFixedDelayValidation() {
        assertThrows(IllegalArgumentException.class, () -> new FixedDelayRetryPolicy(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new FixedDelayRetryPolicy(Duration.ofSeconds(-1)));
    }

    @Test
    @DisplayName("ExponentialBackoff without jitter should scale predictably and cap at max")
    void testExponentialBackoffWithoutJitter() {
        // base: 100ms, max: 1000ms, multiplier: 2.0, jitter: false
        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                Duration.ofMillis(100),
                Duration.ofMillis(1000),
                2.0,
                false
        );

        // Attempt 1: 100 * 2^0 = 100ms
        assertEquals(Duration.ofMillis(100), policy.calculateNextBackoff(1));
        // Attempt 2: 100 * 2^1 = 200ms
        assertEquals(Duration.ofMillis(200), policy.calculateNextBackoff(2));
        // Attempt 3: 100 * 2^2 = 400ms
        assertEquals(Duration.ofMillis(400), policy.calculateNextBackoff(3));
        // Attempt 4: 100 * 2^3 = 800ms
        assertEquals(Duration.ofMillis(800), policy.calculateNextBackoff(4));
        // Attempt 5: 100 * 2^4 = 1600ms -> capped at 1000ms
        assertEquals(Duration.ofMillis(1000), policy.calculateNextBackoff(5));
    }

    @Test
    @DisplayName("ExponentialBackoff with jitter should generate bounded random delay")
    void testExponentialBackoffWithJitter() {
        // Inject a controlled fake Random returning 0.5 (midpoint)
        Random deterministicRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.5;
            }
        };

        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                Duration.ofMillis(100),
                Duration.ofMillis(1000),
                2.0,
                true,
                deterministicRandom
        );

        // Attempt 1 without jitter would be 100ms. With nextDouble() = 0.5, delay is 50ms.
        assertEquals(Duration.ofMillis(50), policy.calculateNextBackoff(1));

        // Attempt 3 without jitter would be 400ms. With 0.5, delay is 200ms.
        assertEquals(Duration.ofMillis(200), policy.calculateNextBackoff(3));
    }

    @Test
    @DisplayName("ExponentialBackoff should enforce valid configuration values")
    void testExponentialBackoffValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ExponentialBackoffRetryPolicy(Duration.ZERO, Duration.ofSeconds(10), 2.0, false));
        assertThrows(IllegalArgumentException.class, () -> 
            new ExponentialBackoffRetryPolicy(Duration.ofSeconds(10), Duration.ofSeconds(5), 2.0, false));
        assertThrows(IllegalArgumentException.class, () -> 
            new ExponentialBackoffRetryPolicy(Duration.ofSeconds(1), Duration.ofSeconds(5), 0.5, false));
    }
}