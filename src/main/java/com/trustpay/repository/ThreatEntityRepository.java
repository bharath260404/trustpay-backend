package com.trustpay.repository;

import com.trustpay.model.ThreatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThreatEntityRepository extends JpaRepository<ThreatEntity, Long> {
    Optional<ThreatEntity> findByEntityValue(String entityValue);
}