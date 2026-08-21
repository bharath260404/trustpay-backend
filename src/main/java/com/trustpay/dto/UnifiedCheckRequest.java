
package com.trustpay.dto;

public record UnifiedCheckRequest(
    String inputData,       // URL, UPI ID, Phone Number, or Raw Query
    String inputType        // 'URL', 'UPI_ID', 'PHONE', 'AUTO'
) {}