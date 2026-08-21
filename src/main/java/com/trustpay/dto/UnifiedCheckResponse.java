package com.trustpay.dto;

import java.util.List;

public record UnifiedCheckResponse(
    String inputPayload,
    String detectedType,
    double aggregatedRiskScore,    // 0.0 (Clean) to 1.0 (High Risk/Fraud)
    String finalRiskLevel,          // 'SAFE', 'WARNING', 'BLOCKED'
    boolean databaseBlacklisted,
    int databaseReportCount,
    List<String> detectedRiskFactors,
    String conciseExplanation,
    String recommendedAction
) {}