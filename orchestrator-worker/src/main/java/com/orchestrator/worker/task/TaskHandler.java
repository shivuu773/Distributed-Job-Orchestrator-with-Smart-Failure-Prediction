package com.orchestrator.worker.task;

public interface TaskHandler {
    String getSupportedJobType();
    void execute(String jobId, String payload) throws Exception;
}