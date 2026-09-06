# The FX Transfer API

A Spring Boot backend for creating currency accounts and moving money between them.

The project covers deposits, withdrawals, transfer fees, transaction states and persistence in PostgreSQL.

## Features

- Create and retrieve currency accounts
- Deposit and withdraw account balances
- Transfer money between accounts
- Calculate a fee for each transfer
- Track transfers through `PENDING`, `COMPLETED` and `FAILED` states
- Store transaction history
- Return structured error responses for invalid requests
- Use H2 for local development and PostgreSQL for persistent deployments
- Prevent duplicate transfers by returning the existing result when the same idempotency key is submitted again
