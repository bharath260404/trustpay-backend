package com.trustpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trustpay.dto.UnifiedCheckRequest;
import com.trustpay.dto.UnifiedCheckResponse;
import com.trustpay.service.ThreatOrchestratorService;

@RestController
@RequestMapping("/api/v1/verify")
public class UnifiedCheckController {

    private final ThreatOrchestratorService threatOrchestratorService;

    public UnifiedCheckController(ThreatOrchestratorService threatOrchestratorService) {
        this.threatOrchestratorService = threatOrchestratorService;
    }

    @PostMapping
    public ResponseEntity<UnifiedCheckResponse> verifyEntity(@RequestBody UnifiedCheckRequest request) {
        UnifiedCheckResponse response = threatOrchestratorService.processUnifiedCheck(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<UnifiedCheckResponse> verifyEntityViaParam(
            @RequestParam("data") String data,
            @RequestParam(value = "type", defaultValue = "AUTO") String type) {
        UnifiedCheckResponse response = threatOrchestratorService.processUnifiedCheck(new UnifiedCheckRequest(data, type));
        return ResponseEntity.ok(response);
    }
}