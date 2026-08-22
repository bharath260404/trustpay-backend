package com.trustpay.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "scam_reports")
public class ScamReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    private ThreatEntity threatEntity;

    @Column(name = "reporter_hash", length = 64)
    private String reporterHash;

    @Column(name = "fraud_category", nullable = false, length = 50)
    private String fraudCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public ScamReport() {}

    public ScamReport(ThreatEntity threatEntity, String reporterHash, String fraudCategory, String description) {
        this.threatEntity = threatEntity;
        this.reporterHash = reporterHash;
        this.fraudCategory = fraudCategory;
        this.description = description;
    }

    public Long getId() { return id; }
    public ThreatEntity getThreatEntity() { return threatEntity; }
    public void setThreatEntity(ThreatEntity threatEntity) { this.threatEntity = threatEntity; }
    public String getReporterHash() { return reporterHash; }
    public void setReporterHash(String reporterHash) { this.reporterHash = reporterHash; }
    public String getFraudCategory() { return fraudCategory; }
    public void setFraudCategory(String fraudCategory) { this.fraudCategory = fraudCategory; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
