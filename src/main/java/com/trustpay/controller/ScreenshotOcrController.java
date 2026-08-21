package com.trustpay.controller;

import com.trustpay.dto.OcrScanResponse;
import com.trustpay.engine.ScreenshotOcrEngine;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ocr")
public class ScreenshotOcrController {

    private final ScreenshotOcrEngine ocrEngine;

    public ScreenshotOcrController(ScreenshotOcrEngine ocrEngine) {
        this.ocrEngine = ocrEngine;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrScanResponse> analyzeScreenshot(@RequestParam("file") MultipartFile file) {
        try {
            OcrScanResponse response = ocrEngine.processScreenshot(
                file.getInputStream(),
                file.getOriginalFilename()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new OcrScanResponse(false, null, null, null, false, 1.0, "BLOCKED", e.getMessage())
            );
        }
    }
}