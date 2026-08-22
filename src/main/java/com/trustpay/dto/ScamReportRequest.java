package com.trustpay.dto;

public record ScamReportRequest(
    String entityValue,
    String entityType,
    String fraudCategory,
    String description,
    String reporterContact
) {}
