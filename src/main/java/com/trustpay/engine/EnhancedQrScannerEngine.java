package com.trustpay.engine;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.trustpay.dto.QrScanResponse;
import com.trustpay.entity.BusinessEntity;
import com.trustpay.repository.BusinessEntityRepository;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnhancedQrScannerEngine {

    private final BusinessEntityRepository businessEntityRepository;

    public EnhancedQrScannerEngine(BusinessEntityRepository businessEntityRepository) {
        this.businessEntityRepository = businessEntityRepository;
    }

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
            return createResponse(false, null, null, null, null, null,
                0.90, "BLOCKED", Map.of(), "No valid QR code detected.",
                false, null, null, null, null, false,
                false, "FAKE", new String[]{"No QR code found"}, "No QR code detected");
        }

        String rawContent = result.getText();
        return analyzeUpiPayload(rawContent);
    }

    public QrScanResponse analyzeUpiPayload(String rawContent) {
        if (rawContent == null || !rawContent.toLowerCase().startsWith("upi://pay")) {
            return createResponse(false, rawContent, null, null, null, null,
                0.85, "WARNING", Map.of(), "Not a valid UPI QR code",
                false, null, null, null, null, false,
                false, "FAKE", new String[]{"Invalid UPI format"}, "Not a valid UPI QR code");
        }

        Map<String, String> queryParams = parseQueryParameters(rawContent);
        String pa = queryParams.get("pa");
        String pn = queryParams.get("pn");
        String mc = queryParams.get("mc");
        String am = queryParams.get("am");

        double riskScore = 0.0;
        List<String> fraudReasons = new ArrayList<>();
        boolean isFakeQr = false;

        // Check 1: Missing or Malformed Payee UPI ID
        if (pa == null || !pa.contains("@")) {
            riskScore += 0.50;
            fraudReasons.add("Missing or malformed Payee UPI ID");
            isFakeQr = true;
        }

        // Check 2: Deceptive Handle Keyword Spoofing
        if (pa != null) {
            String lowerPa = pa.toLowerCase();
            if (lowerPa.contains("support") || lowerPa.contains("refund") || 
                lowerPa.contains("verify") || lowerPa.contains("help")) {
                riskScore += 0.40;
                fraudReasons.add("UPI handle contains deceptive keywords");
                isFakeQr = true;
            }
        }

        // Check 3: Business Entity Lookup
        BusinessEntity business = null;
        if (pa != null) {
            business = businessEntityRepository.findByUpiId(pa).orElse(null);
        }

        // Check 4: Name mismatch detection
        if (business != null && pn != null && !pn.isEmpty()) {
            if (!business.getBusinessName().equalsIgnoreCase(pn) && 
                !pn.toLowerCase().contains(business.getBusinessName().toLowerCase())) {
                riskScore += 0.60;
                fraudReasons.add("Payee name doesn't match registered business");
                isFakeQr = true;
            }
        }

        // Check 5: Unregistered UPI ID
        if (pa != null && business == null) {
            riskScore += 0.30;
            fraudReasons.add("UPI ID not found in registered businesses database");
        }

        String riskLevel = riskScore >= 0.70 ? "BLOCKED" : (riskScore >= 0.30 ? "WARNING" : "SAFE");
        String fraudStatus = isFakeQr ? "FAKE" : (riskScore > 0 ? "SUSPICIOUS" : "LEGITIMATE");
        String recommendation = isFakeQr ? 
            "🚨 This QR code appears to be FAKE! Do not make payment." :
            (riskScore > 0 ? "⚠️ Verify with the business before payment" :
            "✅ This QR code appears legitimate");

        return createResponse(
            true, rawContent, pa, pn, mc, am,
            Math.min(riskScore, 1.0), riskLevel, queryParams,
            fraudReasons.isEmpty() ? "Valid UPI QR code" : String.join("; ", fraudReasons),
            business != null,
            business != null ? business.getBusinessName() : null,
            business != null ? business.getOwnerName() : null,
            business != null ? business.getBusinessType() : null,
            business != null ? business.getLocation() : null,
            business != null ? business.getIsVerified() : false,
            isFakeQr,
            fraudStatus,
            fraudReasons.toArray(new String[0]),
            recommendation
        );
    }

    private QrScanResponse createResponse(boolean isValid, String raw, String pa, String pn, 
            String mc, String am, double riskScore, String riskLevel, Map<String,String> params,
            String warning, boolean businessFound, String bizName, String bizOwner,
            String bizType, String bizLocation, boolean bizVerified,
            boolean isFake, String fraudStatus, String[] fraudReasons, String recommendation) {
        return new QrScanResponse(
            isValid, raw, pa, pn, mc, am, riskScore, riskLevel, params, warning,
            businessFound, bizName, bizOwner, bizType, bizLocation, bizVerified,
            isFake, fraudStatus, fraudReasons, recommendation
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