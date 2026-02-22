package com.bhavesh.fxtransfer.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String owner;
    private Double balance;
    private String currency;
    private LocalDateTime createdAt;
}
