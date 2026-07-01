package com.pesamjanja.app.domain.model

/** High-level kind of money movement detected/entered. */
enum class TransactionType {
    RECEIVED,
    SENT,
    AIRTIME,
    BUNDLES,
    WITHDRAWAL
}

/** Where a transaction row came from. */
enum class TransactionSource {
    MANUAL,
    AUTO_DETECTED
}

/** Owed-to-me vs I-owe, for the Den (Debt) Tracker. */
enum class DebtDirection {
    OWED_TO_ME,
    I_OWE
}
