package com.pesamjanja.app.service.parser

/**
 * Maps known merchant/till names or numbers (as they appear in M-Pesa
 * notification text) to a suggested category name. Add new entries here —
 * parsing logic in MpesaMessageParser never needs to change.
 *
 * Lookups are case-insensitive and match on a normalized (lowercased,
 * trimmed) version of the counterparty/till text.
 */
object MerchantCategoryMap {

    // Till number / merchant name (lowercase) -> category name (must match a Category.name seed)
    private val tillToCategory: Map<String, String> = mapOf(
        // Food / campus eateries — replace with real till numbers per campus
        "java house" to "Food",
        "kfc" to "Food",
        "naivas" to "Food",
        "quickmart" to "Food",
        "mama oliech" to "Food",

        // Transport
        "uber" to "Boda/Uber",
        "bolt" to "Boda/Uber",
        "little cab" to "Boda/Uber",

        // Bundles / data
        "safaricom data" to "Bundles/Data",

        // Printing / stationery
        "text book centre" to "Notes/Printing",
        "copy cat" to "Notes/Printing",
    )

    /**
     * @param tillOrName raw merchant identifier parsed out of the SMS body
     * (could be a till number, paybill name, or recipient name)
     * @return suggested category name, or null if no match — caller leaves
     * category unset for manual assignment
     */
    fun suggestCategory(tillOrName: String?): String? {
        if (tillOrName.isNullOrBlank()) return null
        val key = tillOrName.trim().lowercase()
        return tillToCategory[key]
    }

    fun addMapping(tillOrName: String, category: String) {
        // For a future "remember my choice" feature: persisting custom mappings
        // would go through Room rather than this in-memory map. Left as a TODO
        // hook since the brief scopes this build pass to the static map only.
    }
}
