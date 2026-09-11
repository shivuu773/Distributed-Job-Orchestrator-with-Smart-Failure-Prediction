/**
 * ============================================================================
 * INTERFACE: SchedulingStrategy
 * ============================================================================
 * WHAT: Strategy contract for ordering pending jobs before dispatch.
 * WHY : Decouples queue sorting algorithms from the orchestration engine.
 * HOW : Provides a Comparator<Job> used by in-memory queues or DB queries.
 * ============================================================================
 */
package com.orchestrator.core.strategy.scheduling;

import com.orchestrator.core.domain.Job;
import java.util.Comparator;

public interface SchedulingStrategy {
    Comparator<Job> getComparator();
}