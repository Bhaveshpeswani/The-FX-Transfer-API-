package com.bhavesh.fxtransfer.service;

import com.bhavesh.fxtransfer.dto.TransactionResponse;
import com.bhavesh.fxtransfer.dto.TransferRequest;
import com.bhavesh.fxtransfer.entity.Account;
import com.bhavesh.fxtransfer.entity.Transaction;
import com.bhavesh.fxtransfer.exception.InsufficientBalanceException;
import com.bhavesh.fxtransfer.exception.ResourceNotFoundException;
import com.bhavesh.fxtransfer.repository.AccountRepository;
import com.bhavesh.fxtransfer.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FxRateService fxRateService;

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           FxRateService fxRateService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.fxRateService = fxRateService;
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        // IDEMPOTENCY CHECK
        // Same key = same request sent twice = return original, don't process again
        Optional<Transaction> existing =
                transactionRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        // Validate both accounts exist
        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Source account not found with id: " + request.getFromAccountId()));

        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Target account not found with id: " + request.getToAccountId()));

        // Validate amount
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero");
        }

        // Validate not transferring to same account
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account");
        }

        // Get live exchange rate and calculate fee
        Double rate = fxRateService.getRate(from.getCurrency(), to.getCurrency());
        Double fee = fxRateService.calculateFee(request.getAmount());
        Double totalDeducted = request.getAmount() + fee;
        Double convertedAmount = request.getAmount() * rate;

        // Build transaction record — starts as PENDING
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(from.getId());
        transaction.setToAccountId(to.getId());
        transaction.setAmount(request.getAmount());
        transaction.setFee(fee);
        transaction.setFromCurrency(from.getCurrency());
        transaction.setToCurrency(to.getCurrency());
        transaction.setExchangeRate(rate);
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setStatus("PENDING");
        transaction.setFailureReason(null);

        // Check if sender has enough balance
        if (totalDeducted > from.getBalance()) {
            transaction.setStatus("FAILED");
            transaction.setFailureReason(
                    "Insufficient balance. Required: " + totalDeducted
                            + " " + from.getCurrency()
                            + ", Available: " + from.getBalance());
            transactionRepository.save(transaction);
            throw new InsufficientBalanceException(transaction.getFailureReason());
        }

        // Execute transfer
        from.setBalance(from.getBalance() - totalDeducted);
        to.setBalance(to.getBalance() + convertedAmount);

        accountRepository.save(from);
        accountRepository.save(to);

        transaction.setStatus("COMPLETED");
        transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getTransactionHistory(Long accountId) {
        // Verify account exists first
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with id: " + accountId));

        return transactionRepository
                .findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction t) {
        TransactionResponse response = new TransactionResponse();
        response.setId(t.getId());
        response.setFromAccountId(t.getFromAccountId());
        response.setToAccountId(t.getToAccountId());
        response.setAmount(t.getAmount());
        response.setFee(t.getFee());
        response.setFromCurrency(t.getFromCurrency());
        response.setToCurrency(t.getToCurrency());
        response.setExchangeRate(t.getExchangeRate());
        response.setStatus(t.getStatus());
        response.setFailureReason(t.getFailureReason());
        response.setCreatedAt(t.getCreatedAt());
        return response;
    }
}
