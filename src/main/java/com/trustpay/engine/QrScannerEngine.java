package com.trustpay.engine;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.trustpay.dto.QrScanResponse;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrScannerEngine {

    public QrScanResponse decodeAndAnalyzeQr(InputStream imageInputStream) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(imageInputStream);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Invalid or corrupt image file uploaded.");
        }

        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result;
        try {
            result = new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
            return new QrScanResponse(
                false, null, null, null, null, null,
                0.90, "BLOCKED", Map.of(), "No valid QR code matrix detected in the uploaded image."
            );
        }

        String rawContent = result.getText();
        return analyzeUpiPayload(rawContent);
    }

    public QrScanResponse analyzeUpiPayload(String rawContent) {
        if (rawContent == null || !rawContent.toLowerCase().startsWith("upi://pay")) {
            return new QrScanResponse(
                false, rawContent, null, null, null, null,
                0.85, "WARNING", Map.of(), "QR Code detected, but payload does not conform to the standard NPCI UPI URI scheme."
            );
        }

        Map<String, String> queryParams = parseQueryParameters(rawContent);
        String pa = queryParams.get("pa"); // Payee Virtual Payment Address
        String pn = queryParams.get("pn"); // Payee Name
        String mc = queryParams.get("mc"); // Merchant Category Code
        String am = queryParams.get("am"); // Fixed Amount (if embedded)

        double riskScore = 0.0;
        StringBuilder warning = new StringBuilder();

        // Check 1: Missing or Malformed Payee UPI ID
        if (pa == null || !pa.contains("@")) {
            riskScore += 0.50;
            warning.append("Missing or malformed Payee UPI ID (pa). ");
        }

        // Check 2: Deceptive Handle Keyword Spoofing
        if (pa != null) {
            String lowerPa = pa.toLowerCase();
            if (lowerPa.contains("support") || lowerPa.contains("refund") || lowerPa.contains("verify") || lowerPa.contains("help")) {
                riskScore += 0.40;
                warning.append("UPI handle contains deceptive keywords commonly used in social engineering scams. ");
            }
        }

        String riskLevel = riskScore >= 0.70 ? "BLOCKED" : (riskScore >= 0.30 ? "WARNING" : "SAFE");
        if (warning.length() == 0) {
            warning.append("Valid NPCI UPI QR code structure.");
        }

        return new QrScanResponse(
            true, rawContent, pa, pn, mc, am,
            Math.min(riskScore, 1.0), riskLevel, queryParams, warning.toString()
        );
    }

    private Map<String, String> parseQueryParameters(String uriString) {
        Map<String, String> params = new HashMap<>();
        if (uriString == null || !uriString.contains("?")) {
            return params;
        }

        try {
            String queryString = uriString.substring(uriString.indexOf("?") + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = idx < pair.length() - 1
                        ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
                        : "";
                    params.put(key, value);
                }
            }
        } catch (Exception ignored) {}

        return params;
    }
}