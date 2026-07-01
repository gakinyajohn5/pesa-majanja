package com.pesamjanja.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pesamjanja.app.ui.addexpense.AddExpenseScreen
import com.pesamjanja.app.ui.dashboard.DashboardScreen
import com.pesamjanja.app.ui.debttracker.DebtTrackerScreen
import com.pesamjanja.app.ui.helbsetup.HelbSetupScreen
import com.pesamjanja.app.ui.onboarding.OnboardingScreen
import com.pesamjanja.app.ui.privacy.PrivacyPolicyScreen
import com.pesamjanja.app.ui.review.ReviewPendingScreen
import com.pesamjanja.app.ui.splitbill.SplitBillScreen

@Composable
fun PesaNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = PesaRoute.Onboarding.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(PesaRoute.Onboarding.route) {
            OnboardingScreen(
                onPermissionFlowStarted = {
                    navController.navigate(PesaRoute.HelbSetup.route) {
                        popUpTo(PesaRoute.Onboarding.route) { inclusive = true }
                    }
                },
                onSkipForNow = {
                    navController.navigate(PesaRoute.HelbSetup.route) {
                        popUpTo(PesaRoute.Onboarding.route) { inclusive = true }
                    }
                },
                onOpenPrivacyPolicy = { navController.navigate(PesaRoute.PrivacyPolicy.route) }
            )
        }

        composable(PesaRoute.HelbSetup.route) {
            HelbSetupScreen(
                onPlanLocked = {
                    navController.navigate(PesaRoute.Dashboard.route) {
                        popUpTo(PesaRoute.HelbSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(PesaRoute.Dashboard.route) {
            DashboardScreen(
                onAddExpense = { navController.navigate(PesaRoute.AddExpense.route) },
                onOpenSplitBill = { navController.navigate(PesaRoute.SplitBill.route) },
                onOpenDebtTracker = { navController.navigate(PesaRoute.DebtTracker.route) },
                onOpenHelbSetup = { navController.navigate(PesaRoute.HelbSetup.route) }
            )
        }

        composable(PesaRoute.ReviewPending.route) {
            ReviewPendingScreen()
        }

        composable(PesaRoute.AddExpense.route) {
            AddExpenseScreen(onSaved = { navController.popBackStack() })
        }

        composable(PesaRoute.SplitBill.route) {
            SplitBillScreen()
        }

        composable(PesaRoute.DebtTracker.route) {
            DebtTrackerScreen()
        }

        composable(PesaRoute.PrivacyPolicy.route) {
            PrivacyPolicyScreen()
        }
    }
}
