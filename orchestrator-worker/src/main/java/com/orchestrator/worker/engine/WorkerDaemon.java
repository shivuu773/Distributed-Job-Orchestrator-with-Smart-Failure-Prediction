package com.orchestrator.worker.engine;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkerDaemon {

    private final TaskExecutorPool taskExecutorPool;

    public WorkerDaemon(TaskExecutorPool taskExecutorPool) {
        this.taskExecutorPool = taskExecutorPool;
    }

    // Runs every 3 seconds to poll for new jobs from Orchestrator
    @Scheduled(fixedDelay = 3000)
    public void pollAndExecuteTasks() {
        try {
            // TODO: Call Orchestrator API to fetch pending tasks/jobs
            // Example response data mock for testing:
            // String jobId = "job-123";
            // String jobType = "PAYMENT_PROCESSING";
            // String payload = "{ \"amount\": 500 }";

            // If a task is received from Orchestrator, submit it to thread pool:
            // taskExecutorPool.submitTask(jobId, jobType, payload);
            
            // System.out.println("Polling orchestrator for available jobs...");

        } catch (Exception e) {
            System.err.println("Error while polling for tasks: " + e.getMessage());
        }
    }
}