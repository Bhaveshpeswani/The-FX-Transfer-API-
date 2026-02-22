package com.bhavesh.fxtransfer.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private String owner;
    private Double initialBalance;
    private String currency;
}
