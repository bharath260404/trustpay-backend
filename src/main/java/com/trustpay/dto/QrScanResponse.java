package com.trustpay.dto;

import java.util.Map;

public record QrScanResponse(
    boolean isValidUpiQr,
    String rawPayload,
    String payeeVpa,        // 'pa' parameter (UPI ID)
    String payeeName,       // 'pn' parameter
    String merchantCode,    // 'mc' parameter
    String amount,          // 'am' parameter
    double riskScore,       // 0.0 (Safe) to 1.0 (High Risk)
    String riskLevel,       // 'SAFE', 'WARNING', 'BLOCKED'
    Map<String, String> parsedParameters,
    String warningMessage
) {}