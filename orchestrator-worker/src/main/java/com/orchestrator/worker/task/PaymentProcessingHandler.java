package com.orchestrator.worker.task;

import org.springframework.stereotype.Component;

@Component
public class PaymentProcessingHandler implements TaskHandler {

    @Override
    public String getSupportedJobType() {
        return "PAYMENT_PROCESSING";
    }

    @Override
    public void execute(String jobId, String payload) throws Exception {
        System.out.println("Processing payment for Job ID: " + jobId + " with payload: " + payload);
        Thread.sleep(3000); 
        
        if (Math.random() < 0.1) {
            throw new RuntimeException("Payment gateway timeout/failure!");
        }

        System.out.println("Payment successfully processed for Job ID: " + jobId);
    }
}