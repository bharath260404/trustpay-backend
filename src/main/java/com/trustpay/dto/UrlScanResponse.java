package com.trustpay.dto;

import java.util.List;

public record UrlScanResponse(
    String inputUrl,
    String parsedDomain,
    String scheme,
    boolean isIpHost,
    boolean isHighRiskTld,
    boolean containsDeceptiveKeywords,
    double riskScore,       // 0.0 (Safe) to 1.0 (High Risk/Phishing)
    String riskLevel,       // 'SAFE', 'WARNING', 'BLOCKED'
    List<String> riskFactors,
    String warningMessage
) {}