package com.bhavesh.fxtransfer.service;

import org.springframework.stereotype.Service;
import com.bhavesh.fxtransfer.dto.*;
import com.bhavesh.fxtransfer.entity.Account;
import com.bhavesh.fxtransfer.exception.InsufficientBalanceException;
import com.bhavesh.fxtransfer.exception.ResourceNotFoundException;
import com.bhavesh.fxtransfer.repository.AccountRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse createAccount(AccountRequest request) {
        if (request.getInitialBalance() < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        String currency = request.getCurrency().toUpperCase();
        if (!List.of("EUR", "USD", "GBP", "INR", "SEK", "NOK").contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }

        Account account = new Account();
        account.setOwner(request.getOwner());
        account.setBalance(request.getInitialBalance());
        account.setCurrency(currency);

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with id: " + id));
        return mapToResponse(account);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse deposit(Long accountId, DepositWithdrawRequest request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with id: " + accountId));

        account.setBalance(account.getBalance() + request.getAmount());
        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse withdraw(Long accountId, DepositWithdrawRequest request) {
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with id: " + accountId));

        if (request.getAmount() > account.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: "
                            + account.getBalance() + " " + account.getCurrency());
        }

        account.setBalance(account.getBalance() - request.getAmount());
        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse mapToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setOwner(account.getOwner());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }
}
