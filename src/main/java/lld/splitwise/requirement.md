# Splitwise Requirements & Design Clarifications

This document outlines the functional requirements, architectural choices, and design considerations for the Splitwise low-level design (LLD) package.

---

## 1. Split Types (Strategy Pattern)
The system supports four split types for dividing expenses among users. To handle these dynamically and ensure extensibility, we utilize the **Strategy Pattern**.

*   **EQUAL**: The expense amount is divided equally among all participating users.
*   **EXACT**: Participating users are assigned exact individual share amounts. The sum of these individual shares must exactly equal the total expense amount.
*   **PERCENT**: Participating users are assigned percentage shares. The sum of all percentages must equal exactly `100%`.
*   **SHARES**: Participating users are assigned integer shares (e.g., User A has 2 shares, User B has 1 share). The expense is split proportionally according to each user's share ratio relative to the total shares.

---

## 2. Groups vs. Flat User-to-User Expenses
To ensure flexibility, the system supports both group-based and flat non-group expenses:
*   **Group Expenses**: Expenses can be created within a group, where only the group members participate.
*   **Flat (Non-Group) Expenses**: Expenses can exist independently of any group, allowing flat user-to-user billing and balance tracking.

---

## 3. Settlement Simplification (Minimizing Transactions)
*   **Requirement**: The system **implements settlement simplification** (also known as debt simplification/minimization).
*   **Approach**: An algorithm (typically utilizing a greedy approach with a max heap and a min heap or a backtracking flow network) is employed to consolidate balances across all users. This reduces the total number of transactions needed to settle all debts (e.g., if User A owes User B $10, and User B owes User C $10, User A will pay User C $10 directly).

---

## 4. Balances: Stored vs. Derived
*   **Design Decision**: Balances are **derived from the expense log** rather than statically stored as source-of-truth fields in user objects.
*   **Rationale**:
    *   **Data Integrity**: Deriving balances from the transaction/expense log prevents synchronization bugs and provides a complete audit trail.
    *   **Caching Strategy**: To handle frequent reads ("hot balances") efficiently, derived balances can be cached. The cache is invalidated or updated incrementally whenever a new expense is logged or a settlement is recorded.

---

## 5. Multi-Currency Support
*   **Scope**: Multi-currency is **out of scope** for the initial implementation. All expenses are assumed to be in a single default currency (e.g., USD or INR).
*   **Extension Points (Plug-in design)**:
    *   If multi-currency support is introduced, it would plug into the **Expense Creator/Validator** and the **Balance Ledger**.
    *   An `ExchangeRateService` interface will be introduced to handle conversion rates at the time of expense creation.
    *   Balances would be stored or computed with currency-specific buckets (e.g., `Map<Currency, BigDecimal>`) instead of a single scalar balance, allowing users to settle debts in the respective currency or convert using prevailing rates.
