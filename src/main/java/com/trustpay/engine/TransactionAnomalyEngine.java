package com.trustpay.engine;

import com.trustpay.dto.TransactionRiskRequest;
import com.trustpay.dto.TransactionRiskResponse;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class TransactionAnomalyEngine {

    private final Map<String, Deque<Long>> velocityTracker = new ConcurrentHashMap<>();

    private static final Set<String> DECEPTIVE_COLLECT_KEYWORDS = Set.of(
        "refund", "cashback", "prize", "reward", "lottery", "bonus", "electricity", "kyc", "overdue", "bill"
    );

    private static final List<Double> PROBING_AMOUNTS = List.of(1.0, 2.0, 5.0, 10.0, 99.0);

    public TransactionRiskResponse evaluateTransaction(TransactionRiskRequest request) {
        String receiver = request.receiverVpa() != null ? request.receiverVpa().trim().toLowerCase() : "unknown";
        String sender = request.senderVpa() != null ? request.senderVpa().trim().toLowerCase() : "unknown";
        String txType = request.transactionType() != null ? request.transactionType().toUpperCase() : "PAY";
        double amount = request.amount() != null ? request.amount() : 0.0;
        String note = request.transactionNote() != null ? request.transactionNote().toLowerCase() : "";

        double riskScore = 0.0;
        List<String> triggers = new ArrayList<>();

        long currentTime = System.currentTimeMillis();
        int velocityCount = recordAndCountVelocity(receiver, currentTime, 60000);

        if (velocityCount >= 5) {
            riskScore += 0.50;
            triggers.add("High velocity burst: " + velocityCount + " requests targeted at entity within 60 seconds");
        } else if (velocityCount >= 3) {
            riskScore += 0.25;
            triggers.add("Moderate velocity alert: " + velocityCount + " requests within 60 seconds");
        }

        if ("COLLECT".equalsIgnoreCase(txType)) {
            boolean hasDeceptiveKeyword = DECEPTIVE_COLLECT_KEYWORDS.stream().anyMatch(note::contains);
            if (hasDeceptiveKeyword) {
                riskScore += 0.60;
                triggers.add("Collect request uses deceptive note keywords to simulate a refund/cashback incentive");
            }
        }

        if (PROBING_AMOUNTS.contains(amount) && "COLLECT".equalsIgnoreCase(txType)) {
            riskScore += 0.30;
            triggers.add("Common probing micro-amount (?" + amount + ") used in collect request");
        }

        LocalTime now = LocalTime.now();
        boolean isOffHours = (now.isAfter(LocalTime.of(1, 0)) && now.isBefore(LocalTime.of(4, 30)));
        if (isOffHours && amount >= 25000.0) {
            riskScore += 0.35;
            triggers.add("High-value transfer (?" + amount + ") initiated during high-risk off-hours (01:00-04:30)");
        }

        double finalScore = Math.min(riskScore, 1.0);
        String riskLevel = finalScore >= 0.70 ? "BLOCKED" : (finalScore >= 0.35 ? "WARNING" : "SAFE");

        String advisory = "BLOCKED".equals(riskLevel)
            ? "CRITICAL: High probability of collect request fraud or velocity attack. Reject transaction."
            : ("WARNING".equals(riskLevel)
                ? "CAUTION: Suspicious behavioral patterns detected. Ensure you are not approving an unsolicited request."
                : "Standard transaction parameters verified.");

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return new TransactionRiskResponse(
            transactionId,
            sender,
            receiver,
            amount,
            txType,
            velocityCount,
            finalScore,
            riskLevel,
            triggers,
            advisory
        );
    }

    private int recordAndCountVelocity(String vpa, long timestamp, long windowMillis) {
        Deque<Long> timestamps = velocityTracker.computeIfAbsent(vpa, k -> new ConcurrentLinkedDeque<>());
        timestamps.addLast(timestamp);

        long cutoff = timestamp - windowMillis;
        while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
            timestamps.pollFirst();
        }

        return timestamps.size();
    }
}
