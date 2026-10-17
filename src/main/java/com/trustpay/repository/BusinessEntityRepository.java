package com.trustpay.repository;

import com.trustpay.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for BusinessEntity - Database access for registered businesses
 */
@Repository
public interface BusinessEntityRepository extends JpaRepository<BusinessEntity, UUID> {
    
    Optional<BusinessEntity> findByUpiId(String upiId);
    
    Optional<BusinessEntity> findByBusinessNameContainingIgnoreCase(String businessName);
    
    List<BusinessEntity> findByIsVerifiedTrue();
    
    List<BusinessEntity> findByBusinessTypeContainingIgnoreCase(String businessType);
    
    @Query("SELECT b FROM BusinessEntity b WHERE LOWER(b.upiId) LIKE LOWER(:pattern)")
    List<BusinessEntity> findByUpiIdContaining(@Param("pattern") String pattern);
    
    boolean existsByUpiId(String upiId);
}