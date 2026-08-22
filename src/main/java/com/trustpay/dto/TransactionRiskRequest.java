package com.trustpay.dto;

public record TransactionRiskRequest(
    String senderVpa,
    String receiverVpa,
    Double amount,
    String transactionType,
    String transactionNote,
    String deviceId
) {}
