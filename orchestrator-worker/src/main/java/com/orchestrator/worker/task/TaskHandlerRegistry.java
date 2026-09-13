package com.orchestrator.worker.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class TaskHandlerRegistry {

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    public TaskHandlerRegistry(List<TaskHandler> handlers) {
        for (TaskHandler handler : handlers) {
            handlerMap.put(handler.getSupportedJobType(), handler);
        }
    }

    public TaskHandler getHandler(String jobType) {
        TaskHandler handler = handlerMap.get(jobType);
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for job type: " + jobType);
        }
        return handler;
    }
}