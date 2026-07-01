package com.pesamjanja.app.service.parser

import com.pesamjanja.app.domain.model.TransactionType
import java.util.regex.Pattern

/**
 * Parses M-Pesa/Safaricom notification text into a structured transaction.
 *
 * M-Pesa message wording has changed over the years and varies by
 * transaction type, so every pattern lives here in one place — tune these
 * without touching the listener service or anything downstream. If a
 * notification doesn't match anything below, [parse] returns null and the
 * caller is expected to log the raw text for future tuning rather than crash.
 */
object MpesaMessageParser {

    // Amount always shows as "Ksh1,234.00" or "KES 1,234.00" — comma-grouped, 2dp.
    private val AMOUNT_PATTERN: Pattern =
        Pattern.compile("(?:Ksh|KES)\\s?([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{2})?)", Pattern.CASE_INSENSITIVE)

    private val RECEIVED_PATTERN: Pattern =
        Pattern.compile("confirmed\\.?\\s*You have received", Pattern.CASE_INSENSITIVE)
    private val RECEIVED_FROM_PATTERN: Pattern =
        Pattern.compile("received\\s+(?:Ksh|KES)\\s?[0-9.,]+\\s+from\\s+([A-Za-z' .-]+?)\\s+\\d", Pattern.CASE_INSENSITIVE)

    private val AIRTIME_PATTERN: Pattern =
        Pattern.compile("airtime", Pattern.CASE_INSENSITIVE)

    private val BUNDLES_PATTERN: Pattern =
        Pattern.compile("bundle|data\\s?(plan|bundle)?", Pattern.CASE_INSENSITIVE)

    private val WITHDRAW_PATTERN: Pattern =
        Pattern.compile("withdraw", Pattern.CASE_INSENSITIVE)

    private val SENT_PATTERN: Pattern =
        Pattern.compile("sent\\s+to|paid\\s+to", Pattern.CASE_INSENSITIVE)
    private val SENT_RECIPIENT_PATTERN: Pattern =
        Pattern.compile("(?:sent\\s+to|paid\\s+to)\\s+([A-Za-z0-9' .-]+?)(?:\\s+for|\\s+on|\\.|$)", Pattern.CASE_INSENSITIVE)

    /**
     * @param text the notification's text content (title + body concatenated is fine)
     * @return a parsed result, or null if nothing matched a known pattern
     */
    fun parse(text: String): ParsedMpesaMessage? {
        val amount = extractAmount(text) ?: return null

        val type = when {
            RECEIVED_PATTERN.matcher(text).find() -> TransactionType.RECEIVED
            AIRTIME_PATTERN.matcher(text).find() -> TransactionType.AIRTIME
            BUNDLES_PATTERN.matcher(text).find() -> TransactionType.BUNDLES
            WITHDRAW_PATTERN.matcher(text).find() -> TransactionType.WITHDRAWAL
            SENT_PATTERN.matcher(text).find() -> TransactionType.SENT
            else -> return null // unrecognized pattern — caller should log raw text, not crash
        }

        val counterparty = when (type) {
            TransactionType.RECEIVED -> firstGroup(RECEIVED_FROM_PATTERN, text)
            TransactionType.SENT -> firstGroup(SENT_RECIPIENT_PATTERN, text)
            else -> null
        }?.trim()

        val suggestedCategory = when (type) {
            TransactionType.AIRTIME -> "Airtime"
            TransactionType.BUNDLES -> "Bundles/Data"
            TransactionType.SENT -> MerchantCategoryMap.suggestCategory(counterparty)
            TransactionType.WITHDRAWAL -> null // left unset for manual assignment, per brief
            TransactionType.RECEIVED -> null
        }

        return ParsedMpesaMessage(
            amount = amount,
            type = type,
            counterparty = counterparty,
            suggestedCategory = suggestedCategory,
            rawText = text
        )
    }

    private fun extractAmount(text: String): Double? {
        val matcher = AMOUNT_PATTERN.matcher(text)
        if (!matcher.find()) return null
        val raw = matcher.group(1) ?: return null
        return raw.replace(",", "").toDoubleOrNull()
    }

    private fun firstGroup(pattern: Pattern, text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) matcher.group(1) else null
    }
}
