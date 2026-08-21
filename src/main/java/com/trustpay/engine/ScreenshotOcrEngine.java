package com.trustpay.engine;

import com.trustpay.dto.OcrScanResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScreenshotOcrEngine {

    public OcrScanResponse processScreenshot(InputStream imageInputStream, String originalFilename) throws Exception {
        // Create temporary image file
        File tempImage = File.createTempFile("ocr_input_", "_" + (originalFilename != null ? originalFilename : "sample.jpg"));
        tempImage.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempImage)) {
            imageInputStream.transferTo(out);
        }

        String extractedText;
        try {
            extractedText = executeTesseractCli(tempImage);
        } catch (Exception e) {
            return new OcrScanResponse(
                false, null, null, null, false, 0.95, "BLOCKED",
                "OCR Engine Error: " + e.getMessage()
            );
        } finally {
            if (tempImage.exists()) {
                tempImage.delete();
            }
        }

        return analyzeExtractedText(extractedText);
    }

    private String executeTesseractCli(File imageFile) throws Exception {
        // Run native CLI: tesseract <imageFile> stdout -l eng
        ProcessBuilder processBuilder = new ProcessBuilder(
            "tesseract",
            imageFile.getAbsolutePath(),
            "stdout",
            "-l", "eng"
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(10, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Tesseract OCR process timed out.");
        }

        return output.toString().trim();
    }

    public OcrScanResponse analyzeExtractedText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new OcrScanResponse(
                false, "", null, null, false, 0.90, "BLOCKED",
                "No readable text found in screenshot. Likely corrupted or heavily edited image."
            );
        }

        // 1. Extract 12-Digit UTR / Ref Number
        String utr = extractPattern(text, "(?i)(?:UTR|Ref|Txn\\s*ID|Transaction\\s*ID)\\s*[:|-]?\\s*(\\d{12})");
        if (utr == null) {
            utr = extractPattern(text, "\\b\\d{12}\\b");
        }

        // 2. Extract Transaction Amount
        String amount = extractPattern(text, "(?:₹|Rs\\.?|INR)\\s*([0-9,]+(?:\\.[0-9]{2})?)");

        // 3. Status Verification Keywords
        boolean hasStatusKeyword = checkKeyword(text, "(?i)(Paid Successfully|Transfer Successful|Completed|Sent to|Payment Successful)");

        double anomalyScore = 0.0;
        StringBuilder warnings = new StringBuilder();

        if (utr == null) {
            anomalyScore += 0.45;
            warnings.append("Missing 12-digit UTR/Ref number. ");
        }

        if (amount == null) {
            anomalyScore += 0.25;
            warnings.append("Could not detect standard payment amount format. ");
        }

        if (!hasStatusKeyword) {
            anomalyScore += 0.25;
            warnings.append("Missing standard bank completion indicator (e.g., 'Paid Successfully'). ");
        }

        String riskLevel = anomalyScore >= 0.70 ? "BLOCKED" : (anomalyScore >= 0.30 ? "WARNING" : "SAFE");
        if (warnings.length() == 0) {
            warnings.append("Screenshot structure matches standard UPI receipt format.");
        }

        return new OcrScanResponse(
            true,
            text.trim(),
            utr,
            amount,
            utr != null,
            Math.min(anomalyScore, 1.0),
            riskLevel,
            warnings.toString()
        );
    }

    private String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.groupCount() >= 1 ? matcher.group(1) : matcher.group(0);
        }
        return null;
    }

    private boolean checkKeyword(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).find();
    }
}