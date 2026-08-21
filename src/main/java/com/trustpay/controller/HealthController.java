package com.trustpay.controller;

import com.trustpay.repository.ThreatEntityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final ThreatEntityRepository threatEntityRepository;

    public HealthController(ThreatEntityRepository threatEntityRepository) {
        this.threatEntityRepository = threatEntityRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        long entityCount = threatEntityRepository.count();
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "TrustPay Backend Engine",
            "databaseConnected", true,
            "recordedThreatEntities", entityCount
        ));
    }
}