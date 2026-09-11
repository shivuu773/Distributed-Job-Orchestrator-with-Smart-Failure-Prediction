/**
 * ============================================================================
 * STRATEGY: FifoSchedulingStrategy
 * ============================================================================
 * WHAT: First-In, First-Out task ordering implementation.
 * WHY : Guarantees fairness by dispatching oldest submitted jobs first.
 * HOW : Sorts strictly by createdAt timestamp ascending.
 * ============================================================================
 */
package com.orchestrator.core.strategy.scheduling;

import com.orchestrator.core.domain.Job;
import java.util.Comparator;

public class FifoSchedulingStrategy implements SchedulingStrategy {

    @Override
    public Comparator<Job> getComparator() {
        return Comparator.comparing(Job::getCreatedAt);
    }
}