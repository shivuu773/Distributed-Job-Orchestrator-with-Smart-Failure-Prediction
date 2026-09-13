package com.orchestrator.worker.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.orchestrator.worker.task.TaskHandler;
import com.orchestrator.worker.task.TaskHandlerRegistry;

@Component
public class TaskExecutorPool {

    private final ExecutorService executorService;
    private final TaskHandlerRegistry taskHandlerRegistry;

    // Fixed thread pool of 5 workers running concurrently
    public TaskExecutorPool(TaskHandlerRegistry taskHandlerRegistry) {
        this.taskHandlerRegistry = taskHandlerRegistry;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    // Submit a task to run asynchronously in a background thread
    public void submitTask(String jobId, String jobType, String payload) {
        executorService.submit(() -> {
            try {
                System.out.println("Executing job " + jobId + " of type " + jobType + " on thread: " + Thread.currentThread().getName());
                
                // Get appropriate handler from registry and execute
                TaskHandler handler = taskHandlerRegistry.getHandler(jobType);
                handler.execute(jobId, payload);
                
                System.out.println("Successfully completed job ID: " + jobId);
                
                // TODO: Call API to mark job as COMPLETED
            } catch (Exception e) {
                System.err.println("Failed executing job ID: " + jobId + ". Reason: " + e.getMessage());
                
                // TODO: Call API to mark job as FAILED or trigger retry
            }
        });
    }
}