package com.trustpay.dto;

public record OcrScanResponse(
    boolean textExtracted,
    String rawExtractedText,
    String detectedUtr,
    String detectedAmount,
    boolean hasValidUtr,
    double anomalyScore,    // 0.0 (Clean) to 1.0 (High Risk/Tampered)
    String riskLevel,       // 'SAFE', 'WARNING', 'BLOCKED'
    String warningMessage
) {}