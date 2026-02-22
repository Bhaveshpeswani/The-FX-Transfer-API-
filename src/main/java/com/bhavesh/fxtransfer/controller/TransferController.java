package com.bhavesh.fxtransfer.controller;


import com.bhavesh.fxtransfer.dto.TransactionResponse;
import com.bhavesh.fxtransfer.dto.TransferRequest;
import com.bhavesh.fxtransfer.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    // POST /api/transfers — initiate a transfer
    @PostMapping
    public ResponseEntity<TransactionResponse> transfer(
            @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.transfer(request));
    }

    // GET /api/transfers/history/{accountId} — get all transactions for an account
    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(
                transferService.getTransactionHistory(accountId));
    }
}
