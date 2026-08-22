package com.trustpay.controller;

import com.trustpay.dto.ScamReportRequest;
import com.trustpay.dto.ScamReportResponse;
import com.trustpay.service.ScamReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/report")
public class ScamReportController {

    private final ScamReportService scamReportService;

    public ScamReportController(ScamReportService scamReportService) {
        this.scamReportService = scamReportService;
    }

    @PostMapping
    public ResponseEntity<ScamReportResponse> reportThreat(@RequestBody ScamReportRequest request) {
        ScamReportResponse response = scamReportService.submitReport(request);
        return ResponseEntity.ok(response);
    }
}
