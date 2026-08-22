package com.trustpay.dto;

import java.util.List;

public record TransactionRiskResponse(
    String transactionId,
    String senderVpa,
    String receiverVpa,
    Double amount,
    String transactionType,
    int velocityCountLast60s,
    double behavioralRiskScore,
    String riskLevel,
    List<String> anomalyTriggers,
    String advisoryMessage
) {}
