package com.pesamjanja.app.service.parser

import com.pesamjanja.app.domain.model.TransactionType

data class ParsedMpesaMessage(
    val amount: Double,
    val type: TransactionType,
    val counterparty: String?,
    val suggestedCategory: String?,
    val rawText: String
)
