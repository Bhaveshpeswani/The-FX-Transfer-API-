package com.bhavesh.fxtransfer.controller;

import com.bhavesh.fxtransfer.dto.*;
import com.bhavesh.fxtransfer.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST /api/accounts — create a new account
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody AccountRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    // GET /api/accounts/{id} — get one account by id
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    // GET /api/accounts — get all accounts
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // POST /api/accounts/{id}/deposit — deposit into account
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable Long id,
            @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(accountService.deposit(id, request));
    }

    // POST /api/accounts/{id}/withdraw — withdraw from account
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @PathVariable Long id,
            @RequestBody DepositWithdrawRequest request) {
        return ResponseEntity.ok(accountService.withdraw(id, request));
    }
}
