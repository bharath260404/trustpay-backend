package com.trustpay.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "threat_entities")
public class ThreatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_value", nullable = false, unique = true)
    private String entityValue;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "reputation_score", nullable = false)
    private Integer reputationScore = 100;

    @Column(name = "total_reports", nullable = false)
    private Integer totalReports = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_reported_at")
    private OffsetDateTime lastReportedAt;

    public ThreatEntity() {}

    public ThreatEntity(String entityValue, String entityType) {
        this.entityValue = entityValue;
        this.entityType = entityType;
    }

    public Long getId() {
        return id;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(Integer reputationScore) {
        this.reputationScore = reputationScore;
    }

    public Integer getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(Integer totalReports) {
        this.totalReports = totalReports;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getLastReportedAt() {
        return lastReportedAt;
    }

    public void setLastReportedAt(OffsetDateTime lastReportedAt) {
        this.lastReportedAt = lastReportedAt;
    }
}