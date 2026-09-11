/**
 * ============================================================================
 * STRATEGY: RiskWeightedSchedulingStrategy
 * ============================================================================
 * WHAT: Intelligent prioritization strategy using priority and AI risk score.
 * WHY : Ensures urgent tasks run first, while preferring lower failure-risk tasks.
 * HOW : Orders by priority descending, then by predictedRiskScore ascending.
 * ============================================================================
 */
package com.orchestrator.core.strategy.scheduling;

import com.orchestrator.core.domain.Job;
import java.util.Comparator;

public class RiskWeightedSchedulingStrategy implements SchedulingStrategy {

    @Override
    public Comparator<Job> getComparator() {
        return Comparator
                .comparingInt(Job::getPriority).reversed()
                .thenComparing(
                        Job::getPredictedRiskScore,
                        Comparator.nullsLast(Double::compareTo)
                );
    }
}