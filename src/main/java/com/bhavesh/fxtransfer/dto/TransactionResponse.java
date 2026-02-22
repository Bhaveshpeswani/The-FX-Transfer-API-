package com.bhavesh.fxtransfer.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
    private Double fee;
    private String fromCurrency;
    private String toCurrency;
    private Double exchangeRate;
    private String status;
    private String failureReason;
    private LocalDateTime createdAt;
}
