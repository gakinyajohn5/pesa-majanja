package com.pesamjanja.app.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.pesamjanja.app.PesaMjanjaApplication
import com.pesamjanja.app.data.entity.Transaction
import com.pesamjanja.app.domain.model.TransactionSource
import com.pesamjanja.app.service.parser.MpesaMessageParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Listens for M-Pesa/Safaricom notifications, parses them with
 * [MpesaMessageParser], and inserts an unconfirmed [Transaction]. The user
 * confirms or discards each one from the dashboard (see the Confirm/Edit
 * card) -- this service never writes a confirmed transaction itself.
 */
class MPesaNotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn ?: return

        // Known Safaricom/M-Pesa package names. Add more here if a device's
        // notification source differs (e.g. an OEM-bundled messaging app) --
        // never touches the regex parsing logic above.
        if (notification.packageName !in KNOWN_MPESA_PACKAGES) return

        val text = extractText(notification.notification) ?: return
        if (text.isBlank()) return

        val parsed = MpesaMessageParser.parse(text)
        if (parsed == null) {
            // Doesn't match any known pattern yet -- log for future tuning, don't crash.
            Log.w(TAG, "Unmatched M-Pesa notification text: $text")
            return
        }

        scope.launch {
            val app = applicationContext as? PesaMjanjaApplication ?: return@launch
            app.transactionRepository.insert(
                Transaction(
                    amount = parsed.amount,
                    type = parsed.type,
                    category = parsed.suggestedCategory,
                    counterparty = parsed.counterparty,
                    source = TransactionSource.AUTO_DETECTED,
                    timestamp = System.currentTimeMillis(),
                    isConfirmed = false,
                    rawNotificationText = parsed.rawText
                )
            )
        }
    }

    private fun extractText(notification: Notification): String? {
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val body = bigText ?: text ?: return null
        return "$title $body".trim()
    }

    companion object {
        private const val TAG = "MPesaListener"

        // Update this list if Safaricom changes their app's package name,
        // or to add a region-specific variant.
        val KNOWN_MPESA_PACKAGES = setOf(
            "com.safaricom.mpesa",
            "com.safaricom.mysafaricom"
        )
    }
}
