package com.trustpay.service;

import com.trustpay.dto.*;
import com.trustpay.engine.*;
import com.trustpay.model.ThreatEntity;
import com.trustpay.repository.ThreatEntityRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ThreatOrchestratorService {

    private final UrlScannerEngine urlScannerEngine;
    private final QrScannerEngine qrScannerEngine;
    private final ThreatEntityRepository threatEntityRepository;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern UPI_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$");

    public ThreatOrchestratorService(
            UrlScannerEngine urlScannerEngine,
            QrScannerEngine qrScannerEngine,
            ThreatEntityRepository threatEntityRepository) {
        this.urlScannerEngine = urlScannerEngine;
        this.qrScannerEngine = qrScannerEngine;
        this.threatEntityRepository = threatEntityRepository;
    }

    public UnifiedCheckResponse processUnifiedCheck(UnifiedCheckRequest request) {
        String input = request.inputData() != null ? request.inputData().trim() : "";
        if (input.isEmpty()) {
            return new UnifiedCheckResponse(
                input, "UNKNOWN", 1.0, "BLOCKED", false, 0,
                List.of("Empty payload provided"), "Input payload cannot be empty.", "Do not proceed with transaction."
            );
        }

        String detectedType = determineInputType(input, request.inputType());
        double aggregatedScore = 0.0;
        List<String> riskFactors = new ArrayList<>();
        boolean isDbBlacklisted = false;
        int dbReportCount = 0;

        // 1. Database Reputation Lookup
        Optional<ThreatEntity> dbEntity = threatEntityRepository.findByEntityValue(input.toLowerCase());
        if (dbEntity.isPresent()) {
            ThreatEntity entity = dbEntity.get();
            dbReportCount = entity.getTotalReports();
            if (entity.getReputationScore() < 40 || dbReportCount >= 3) {
                isDbBlacklisted = true;
                aggregatedScore += 0.60;
                riskFactors.add("Entity present in community database with " + dbReportCount + " fraud reports");
            }
        }

        // 2. Engine Heuristic Evaluation
        if ("URL".equalsIgnoreCase(detectedType)) {
            UrlScanResponse urlResult = urlScannerEngine.analyzeUrl(input);
            aggregatedScore = Math.max(aggregatedScore, urlResult.riskScore());
            riskFactors.addAll(urlResult.riskFactors());
        } else if ("UPI_QR".equalsIgnoreCase(detectedType) || input.toLowerCase().startsWith("upi://pay")) {
            QrScanResponse qrResult = qrScannerEngine.analyzeUpiPayload(input);
            aggregatedScore = Math.max(aggregatedScore, qrResult.riskScore());
            if (qrResult.warningMessage() != null && !qrResult.warningMessage().contains("Valid NPCI")) {
                riskFactors.add(qrResult.warningMessage());
            }
        } else if ("UPI_ID".equalsIgnoreCase(detectedType)) {
            if (input.toLowerCase().contains("refund") || input.toLowerCase().contains("support") || input.toLowerCase().contains("verify")) {
                aggregatedScore += 0.45;
                riskFactors.add("UPI ID contains deceptive keywords commonly associated with support scams");
            }
        } else if ("PHONE".equalsIgnoreCase(detectedType)) {
            if (isDbBlacklisted) {
                aggregatedScore += 0.30;
                riskFactors.add("Phone number linked to previously reported fraudulent transactions");
            }
        }

        double finalScore = Math.min(aggregatedScore, 1.0);
        String finalLevel = finalScore >= 0.70 ? "BLOCKED" : (finalScore >= 0.30 ? "WARNING" : "SAFE");

        String explanation = riskFactors.isEmpty()
            ? "No security anomalies or community fraud reports detected for this entity."
            : String.join("; ", riskFactors) + ".";

        String action = "BLOCKED".equals(finalLevel)
            ? "CRITICAL RISK: Do not authorize payment or share credentials."
            : ("WARNING".equals(finalLevel)
                ? "MEDIUM RISK: Verify recipient identity through independent channels before transferring funds."
                : "LOW RISK: Entity appears clean, but always verify payment details.");

        return new UnifiedCheckResponse(
            input,
            detectedType,
            finalScore,
            finalLevel,
            isDbBlacklisted,
            dbReportCount,
            riskFactors,
            explanation,
            action
        );
    }

    private String determineInputType(String input, String explicitType) {
        if (explicitType != null && !"AUTO".equalsIgnoreCase(explicitType)) {
            return explicitType.toUpperCase();
        }
        if (input.startsWith("http://") || input.startsWith("https://") || input.contains(".com") || input.contains(".top") || input.contains(".xyz")) {
            return "URL";
        }
        if (input.toLowerCase().startsWith("upi://pay")) {
            return "UPI_QR";
        }
        if (UPI_ID_PATTERN.matcher(input).matches()) {
            return "UPI_ID";
        }
        if (PHONE_PATTERN.matcher(input).matches()) {
            return "PHONE";
        }
        return "GENERIC_TEXT";
    }
}