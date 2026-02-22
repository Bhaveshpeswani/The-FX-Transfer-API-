# The FX Transfer API 

> A robust backend banking system deployed live on the internet. It allows users to create accounts in different currencies, transfer money with real fee logic, and track every transaction with clean error handling.

###  Core Features

* **Account Management:** Create and fetch user accounts across multiple currencies.
* **Core Banking:** Deposit and withdraw funds securely.
* **Smart Transfers:** Transfer money between accounts with automated fee calculation.
* **Transaction Lifecycle:** Track transfer statuses precisely (`PENDING` → `COMPLETED` / `FAILED`).
* **Audit Trail:** Maintain a complete transaction history for every account.
* **Graceful Error Handling:** Clean, user-friendly error messages (no messy stack traces).
* **Database:** Configured to switch seamlessly from an in-memory H2 database to a production-ready PostgreSQL database.
* **Live Deployment:** Hosted and accessible live on Railway.
