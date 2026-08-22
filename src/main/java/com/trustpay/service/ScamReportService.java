package com.trustpay.service;

import com.trustpay.dto.ScamReportRequest;
import com.trustpay.dto.ScamReportResponse;
import com.trustpay.model.ScamReport;
import com.trustpay.model.ThreatEntity;
import com.trustpay.repository.ScamReportRepository;
import com.trustpay.repository.ThreatEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;

@Service
public class ScamReportService {

    private final ThreatEntityRepository threatEntityRepository;
    private final ScamReportRepository scamReportRepository;

    public ScamReportService(ThreatEntityRepository threatEntityRepository, ScamReportRepository scamReportRepository) {
        this.threatEntityRepository = threatEntityRepository;
        this.scamReportRepository = scamReportRepository;
    }

    @Transactional
    public ScamReportResponse submitReport(ScamReportRequest request) {
        String sanitizedEntity = request.entityValue().trim().toLowerCase();
        String entityType = request.entityType() != null ? request.entityType().toUpperCase() : "GENERIC";

        ThreatEntity entity = threatEntityRepository.findByEntityValue(sanitizedEntity)
            .orElseGet(() -> {
                ThreatEntity newEntity = new ThreatEntity(sanitizedEntity, entityType);
                newEntity.setReputationScore(100);
                newEntity.setTotalReports(0);
                return threatEntityRepository.save(newEntity);
            });

        entity.setTotalReports(entity.getTotalReports() + 1);
        entity.setLastReportedAt(OffsetDateTime.now());

        int calculatedScore = Math.max(0, 100 - (entity.getTotalReports() * 20));
        entity.setReputationScore(calculatedScore);
        threatEntityRepository.save(entity);

        String reporterHash = hashReporter(request.reporterContact());
        ScamReport report = new ScamReport(
            entity,
            reporterHash,
            request.fraudCategory() != null ? request.fraudCategory().toUpperCase() : "GENERAL_FRAUD",
            request.description()
        );
        ScamReport savedReport = scamReportRepository.save(report);

        String riskLevel = calculatedScore <= 30 ? "BLOCKED" : (calculatedScore <= 70 ? "WARNING" : "SAFE");

        return new ScamReportResponse(
            savedReport.getId(),
            entity.getEntityValue(),
            entity.getEntityType(),
            entity.getReputationScore(),
            entity.getTotalReports(),
            riskLevel,
            "Threat report recorded successfully. Community reputation updated."
        );
    }

    private String hashReporter(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            return "ANONYMOUS";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contact.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
}
