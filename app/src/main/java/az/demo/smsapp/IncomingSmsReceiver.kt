package az.demo.smsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class IncomingSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val parts = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (parts.isEmpty()) return
        val sender = parts.first().displayOriginatingAddress ?: "Naməlum nömrə"
        val text = parts.joinToString(separator = "") { it.displayMessageBody.orEmpty() }
        if (text.isNotBlank()) SmsStore.add(context, sender, text)
    }
}
