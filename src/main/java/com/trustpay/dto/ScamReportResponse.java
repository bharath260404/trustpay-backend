package com.trustpay.dto;

public record ScamReportResponse(
    Long reportId,
    String entityValue,
    String entityType,
    int updatedReputationScore,
    int totalReports,
    String currentRiskLevel,
    String message
) {}
