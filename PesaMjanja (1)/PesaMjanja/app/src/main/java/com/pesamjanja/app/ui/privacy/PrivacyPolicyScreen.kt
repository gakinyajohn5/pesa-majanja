package com.pesamjanja.app.ui.privacy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Placeholder privacy policy screen, required for the Play Store listing
 * when declaring notification-access (sensitive permission) use.
 * TODO: replace with the real, hosted privacy policy text/URL before release.
 */
@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Privacy Policy", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(
            "Pesa Mjanja reads M-Pesa notifications on your device using Android's " +
                "Notification Listener API to detect transactions automatically. " +
                "This data is stored only on your device's local database and is never " +
                "uploaded to any server. You review and confirm every auto-detected entry " +
                "before it counts toward your budget.\n\n" +
                "We do not request SMS permissions, do not read your messages directly, " +
                "and do not share any data with third parties.\n\n" +
                "[Placeholder — replace with full policy text/URL before Play Store submission.]",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}
