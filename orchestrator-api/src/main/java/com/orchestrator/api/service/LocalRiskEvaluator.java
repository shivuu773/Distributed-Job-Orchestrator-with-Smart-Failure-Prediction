package com.orchestrator.api.service;

import org.springframework.stereotype.Service;

@Service
public class LocalRiskEvaluator {
    public int evaluateRisk(String jobType, String payload) {
        if ("PAYMENT_PROCESSING".equalsIgnoreCase(jobType)) return 8;
        if (payload != null && payload.length() > 2000) return 7;
        if ("EMAIL_NOTIFICATION".equalsIgnoreCase(jobType)) return 2;
        return 5;
    }
    public long estimateDuration(String jobType) {
        if ("REPORT_GENERATION".equalsIgnoreCase(jobType)) return 10000L;
        return 2000L;
    }
}