package com.trustpay.controller;

import com.trustpay.dto.TransactionRiskRequest;
import com.trustpay.dto.TransactionRiskResponse;
import com.trustpay.engine.TransactionAnomalyEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionRiskController {

    private final TransactionAnomalyEngine transactionAnomalyEngine;

    public TransactionRiskController(TransactionAnomalyEngine transactionAnomalyEngine) {
        this.transactionAnomalyEngine = transactionAnomalyEngine;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<TransactionRiskResponse> evaluateTransaction(@RequestBody TransactionRiskRequest request) {
        TransactionRiskResponse response = transactionAnomalyEngine.evaluateTransaction(request);
        return ResponseEntity.ok(response);
    }
}
