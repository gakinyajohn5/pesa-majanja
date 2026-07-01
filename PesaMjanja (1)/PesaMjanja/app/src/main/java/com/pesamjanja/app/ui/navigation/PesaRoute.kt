package com.pesamjanja.app.ui.navigation

/** All destinations from the Step 3 screen list, plus the privacy policy placeholder. */
sealed class PesaRoute(val route: String) {
    data object Onboarding : PesaRoute("onboarding")           // 3a
    data object HelbSetup : PesaRoute("helb_setup")            // 3b
    data object Dashboard : PesaRoute("dashboard")             // 3c
    data object ReviewPending : PesaRoute("review_pending")    // 3e (dedicated review list)
    data object AddExpense : PesaRoute("add_expense")          // 3f
    data object SplitBill : PesaRoute("split_bill")            // 3g
    data object DebtTracker : PesaRoute("debt_tracker")        // 3h
    data object PrivacyPolicy : PesaRoute("privacy_policy")    // Step 5
}
