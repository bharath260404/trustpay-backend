package com.trustpay.engine;

import com.trustpay.dto.UrlScanResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class UrlScannerEngine {

    private static final Set<String> HIGH_RISK_TLDS = Set.of(
        "top", "xyz", "zip", "club", "site", "online", "cc", "tk", "ml", "ga", "cf", "gq", "work"
    );

    private static final Set<String> TARGET_BRANDS = Set.of(
        "sbi", "icici", "hdfc", "axis", "paytm", "phonepe", "gpay", "bhim", "razorpay", "npci"
    );

    private static final Set<String> DECEPTIVE_KEYWORDS = Set.of(
        "kyc", "refund", "verify", "update", "login", "support", "secure", "reward", "cashback", "claim"
    );

    private static final Set<String> URL_SHORTENERS = Set.of(
        "bit.ly", "tinyurl.com", "t.co", "is.gd", "cutt.ly", "rb.gy"
    );

    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(\\d{1,3}\\.){3}\\d{1,3}$"
    );

    public UrlScanResponse analyzeUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return new UrlScanResponse(
                rawUrl, null, null, false, false, false,
                1.0, "BLOCKED", List.of("Empty URL provided"), "URL string cannot be empty."
            );
        }

        String normalizedUrl = rawUrl.trim();
        if (!normalizedUrl.startsWith("http://") && !normalizedUrl.startsWith("https://")) {
            normalizedUrl = "https://" + normalizedUrl;
        }

        URI uri;
        try {
            uri = URI.create(normalizedUrl);
        } catch (Exception e) {
            return new UrlScanResponse(
                rawUrl, null, null, false, false, false,
                0.90, "BLOCKED", List.of("Malformed URL syntax"), "Invalid URL syntax format."
            );
        }

        String host = uri.getHost();
        if (host == null) {
            return new UrlScanResponse(
                rawUrl, null, null, false, false, false,
                0.90, "BLOCKED", List.of("Missing hostname"), "Unable to extract host domain from URL."
            );
        }

        host = host.toLowerCase();
        String scheme = uri.getScheme();

        double riskScore = 0.0;
        List<String> riskFactors = new ArrayList<>();

        // Check 1: IP Address Host Hostname
        boolean isIpHost = IP_PATTERN.matcher(host).matches();
        if (isIpHost) {
            riskScore += 0.70;
            riskFactors.add("Host is a raw IP address instead of a domain name");
        }

        // Check 2: Unencrypted HTTP Protocol
        if ("http".equalsIgnoreCase(scheme)) {
            riskScore += 0.20;
            riskFactors.add("Unencrypted HTTP protocol used for payment interaction");
        }

        // Check 3: High-Risk TLD Check
        String tld = extractTld(host);
        boolean isHighRiskTld = HIGH_RISK_TLDS.contains(tld);
        if (isHighRiskTld) {
            riskScore += 0.40;
            riskFactors.add("Uses high-risk Top-Level Domain (." + tld + ")");
        }

        // Check 4: URL Shorteners
        if (URL_SHORTENERS.contains(host)) {
            riskScore += 0.30;
            riskFactors.add("URL shortener service masks final destination domain");
        }

        // Check 5: Brand Impersonation & Deceptive Keyword Squatting
        boolean containsBrand = containsAny(host, TARGET_BRANDS);
        boolean containsDeceptiveKeyword = containsAny(host, DECEPTIVE_KEYWORDS);
        boolean isDeceptive = containsBrand && containsDeceptiveKeyword;

        if (isDeceptive) {
            riskScore += 0.50;
            riskFactors.add("Domain combines trusted financial brand name with deceptive keywords (Brand Impersonation)");
        } else if (containsDeceptiveKeyword && !isIpHost) {
            riskScore += 0.20;
            riskFactors.add("Domain contains suspicious security/financial keywords");
        }

        // Check 6: Subdomain Depth Obfuscation
        long subdomainCount = host.chars().filter(ch -> ch == '.').count();
        if (subdomainCount >= 3) {
            riskScore += 0.25;
            riskFactors.add("Excessive subdomain depth used for domain obfuscation");
        }

        double finalScore = Math.min(riskScore, 1.0);
        String riskLevel = finalScore >= 0.70 ? "BLOCKED" : (finalScore >= 0.30 ? "WARNING" : "SAFE");

        String warningMessage = riskFactors.isEmpty()
            ? "Domain displays standard security attributes."
            : String.join("; ", riskFactors) + ".";

        return new UrlScanResponse(
            rawUrl,
            host,
            scheme,
            isIpHost,
            isHighRiskTld,
            isDeceptive,
            finalScore,
            riskLevel,
            riskFactors,
            warningMessage
        );
    }

    private String extractTld(String host) {
        int lastDot = host.lastIndexOf('.');
        if (lastDot != -1 && lastDot < host.length() - 1) {
            return host.substring(lastDot + 1);
        }
        return "";
    }

    private boolean containsAny(String text, Set<String> keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}