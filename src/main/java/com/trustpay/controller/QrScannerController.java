package com.trustpay.controller;

import com.trustpay.dto.QrScanResponse;
import com.trustpay.engine.EnhancedQrScannerEngine;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/qr")
public class QrScannerController {

    private final EnhancedQrScannerEngine qrScannerEngine;

    public QrScannerController(EnhancedQrScannerEngine qrScannerEngine) {
        this.qrScannerEngine = qrScannerEngine;
    }

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QrScanResponse> scanQrCode(@RequestParam("file") MultipartFile file) {
        try {
            QrScanResponse response = qrScannerEngine.decodeAndAnalyzeQr(file.getInputStream());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new QrScanResponse(false, null, null, null, null, null, 1.0, "BLOCKED", null, e.getMessage(),
                    false, null, null, null, null, false, false, "FAKE", new String[]{e.getMessage()}, "Error scanning QR")
            );
        }
    }
}