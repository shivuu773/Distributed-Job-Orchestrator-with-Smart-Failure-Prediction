package com.orchestrator.worker.task;

import org.springframework.stereotype.Component;

@Component
public class EmailNotificationHandler implements TaskHandler {

    @Override
    public String getSupportedJobType() {
        return "EMAIL_NOTIFICATION";
    }

    @Override
    public void execute(String jobId, String payload) throws Exception {
        System.out.println("Sending email notification for Job ID: " + jobId);
        Thread.sleep(1500); 
        System.out.println("Email sent successfully for Job ID: " + jobId);
    }
}