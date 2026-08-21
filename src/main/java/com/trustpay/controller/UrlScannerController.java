package com.trustpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trustpay.dto.UrlScanRequest;
import com.trustpay.dto.UrlScanResponse;
import com.trustpay.engine.UrlScannerEngine;

@RestController
@RequestMapping("/api/v1/url")
public class UrlScannerController {

    private final UrlScannerEngine urlScannerEngine;

    public UrlScannerController(UrlScannerEngine urlScannerEngine) {
        this.urlScannerEngine = urlScannerEngine;
    }

    @PostMapping("/scan")
    public ResponseEntity<UrlScanResponse> scanUrl(@RequestBody UrlScanRequest request) {
        UrlScanResponse response = urlScannerEngine.analyzeUrl(request.url());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scan")
    public ResponseEntity<UrlScanResponse> scanUrlViaParam(@RequestParam("url") String url) {
        UrlScanResponse response = urlScannerEngine.analyzeUrl(url);
        return ResponseEntity.ok(response);
    }
}