package az.demo.smsapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Local storage only. No internet or server sync. */
object SmsStore {
    private const val PREFS = "local_sms_inbox"
    private const val ITEMS = "items"

    fun add(context: Context, sender: String, body: String, receivedAt: Long = System.currentTimeMillis()) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = JSONArray(prefs.getString(ITEMS, "[]"))
        val updated = JSONArray()
        updated.put(JSONObject().put("sender", sender).put("body", body).put("time", receivedAt))
        for (i in 0 until minOf(existing.length(), 49)) updated.put(existing.getJSONObject(i))
        prefs.edit().putString(ITEMS, updated.toString()).apply()
    }

    fun all(context: Context): JSONArray {
        return try {
            JSONArray(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(ITEMS, "[]"))
        } catch (_: Exception) { JSONArray() }
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(ITEMS).apply()
    }
}
