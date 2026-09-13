package com.orchestrator.worker.task;

import org.springframework.stereotype.Component;

@Component
public class ReportGenerationHandler implements TaskHandler {

    @Override
    public String getSupportedJobType() {
        return "REPORT_GENERATION";
    }

    @Override
    public void execute(String jobId, String payload) throws Exception {
        System.out.println("Generating heavy report for Job ID: " + jobId);
        Thread.sleep(6000); 
        System.out.println("Report generated successfully for Job ID: " + jobId);
    }
}