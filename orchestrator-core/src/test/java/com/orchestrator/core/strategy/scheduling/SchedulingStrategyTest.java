/**
 * ============================================================================
 * TEST SUITE: SchedulingStrategyTest
 * ============================================================================
 * WHAT: Verifies FIFO and Risk-Weighted queue sorting strategies.
 * WHY : Ensures job ordering invariants function correctly prior to DB integration.
 * HOW : Uses comparator sorts on sample Job instances and asserts order.
 * ============================================================================
 */
package com.orchestrator.core.strategy.scheduling;

import com.orchestrator.core.domain.Job;
import com.orchestrator.core.domain.JobType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulingStrategyTest {

    @Test
    @DisplayName("FifoSchedulingStrategy sorts jobs strictly by creation time ascending")
    void testFifoOrdering() throws InterruptedException {
        Job first = new Job(JobType.EMAIL_NOTIFICATION, "{}", 1, 3);
        Thread.sleep(10); // Guarantee timestamp delta
        Job second = new Job(JobType.REPORT_GENERATION, "{}", 1, 3);

        List<Job> jobs = new ArrayList<>(List.of(second, first));
        jobs.sort(new FifoSchedulingStrategy().getComparator());

        assertEquals(first.getId(), jobs.get(0).getId());
        assertEquals(second.getId(), jobs.get(1).getId());
    }

    @Test
    @DisplayName("RiskWeightedSchedulingStrategy prefers higher priority, then lower risk")
    void testRiskWeightedOrdering() {
        Job lowPriorityLowRisk = new Job(JobType.EMAIL_NOTIFICATION, "{}", 1, 3);
        lowPriorityLowRisk.setPredictedRiskScore(0.1);

        Job highPriorityHighRisk = new Job(JobType.PAYMENT_PROCESSING, "{}", 10, 3);
        highPriorityHighRisk.setPredictedRiskScore(0.9);

        Job highPriorityLowRisk = new Job(JobType.PAYMENT_PROCESSING, "{}", 10, 3);
        highPriorityLowRisk.setPredictedRiskScore(0.2);

        List<Job> jobs = new ArrayList<>(List.of(lowPriorityLowRisk, highPriorityHighRisk, highPriorityLowRisk));
        jobs.sort(new RiskWeightedSchedulingStrategy().getComparator());

        // Order expected:
        // 1. highPriorityLowRisk (Priority 10, Risk 0.2)
        // 2. highPriorityHighRisk (Priority 10, Risk 0.9)
        // 3. lowPriorityLowRisk (Priority 1, Risk 0.1)
        assertEquals(highPriorityLowRisk.getId(), jobs.get(0).getId());
        assertEquals(highPriorityHighRisk.getId(), jobs.get(1).getId());
        assertEquals(lowPriorityLowRisk.getId(), jobs.get(2).getId());
    }
}