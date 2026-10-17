package com.trustpay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BusinessEntity - Represents a registered business/shop for QR verification
 */
@Entity
@Table(name = "business_entities")
public class BusinessEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String businessName;
    
    @Column(nullable = false, unique = true)
    private String upiId;  // e.g., "shopname@upi"
    
    @Column
    private String ownerName;
    
    @Column
    private String businessType; // Kirana, Restaurant, Grocery, etc.
    
    @Column
    private String location;
    
    @Column
    private String phone;
    
    @Column
    private Boolean isVerified;  // true if verified by TrustPay
    
    @Column
    private LocalDateTime registeredAt;
    
    @Column
    private LocalDateTime verifiedAt;
    
    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        if (this.isVerified == null) {
            this.isVerified = false;
        }
    }
    
    // Constructors
    public BusinessEntity() {}
    
    public BusinessEntity(String businessName, String upiId, String ownerName, 
                         String businessType, String location, String phone) {
        this.businessName = businessName;
        this.upiId = upiId;
        this.ownerName = ownerName;
        this.businessType = businessType;
        this.location = location;
        this.phone = phone;
        this.isVerified = false;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}