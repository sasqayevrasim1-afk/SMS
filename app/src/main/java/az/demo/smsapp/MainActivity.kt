package az.demo.smsapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var list: LinearLayout
    private lateinit var status: TextView
    private lateinit var permissionButton: Button
    private val handler = Handler(Looper.getMainLooper())
    private val refresher = object : Runnable {
        override fun run() {
            refreshInbox()
            handler.postDelayed(this, 1600)
        }
    }
    private val bg = Color.rgb(11, 17, 29)
    private val panel = Color.rgb(26, 36, 54)
    private val white = Color.rgb(239, 246, 255)
    private val muted = Color.rgb(160, 175, 194)
    private val accent = Color.rgb(46, 169, 137)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(26), dp(20), dp(20))
            setBackgroundColor(bg)
        }
        val title = label("SMS Bildirişləri", 27f, white, true)
        container.addView(title)
        container.addView(label("Yeni SMS-lər yalnız bu telefonda göstərilir", 13f, muted).apply {
            setPadding(0, dp(8), 0, dp(19))
        })
        status = label("İcazə yoxlanılır...", 15f, white).apply {
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(panel)
        }
        container.addView(status, LinearLayout.LayoutParams(-1, -2))
        permissionButton = Button(this).apply {
            text = "SMS icazəsini ver"
            isAllCaps = false
            setTextColor(Color.WHITE)
            backgroundTintList = android.content.res.ColorStateList.valueOf(accent)
            setOnClickListener { askPermission() }
        }
        container.addView(permissionButton, LinearLayout.LayoutParams(-1, dp(58)).apply {
            topMargin = dp(14)
        })
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(14), 0, dp(8))
        }
        bar.addView(label("Gələn SMS-lər", 18f, white, true), LinearLayout.LayoutParams(0, -2, 1f))
        val clear = Button(this).apply {
            text = "Təmizlə"
            isAllCaps = false
            setTextColor(white)
            setOnClickListener {
                SmsStore.clear(this@MainActivity)
                refreshInbox()
            }
        }
        bar.addView(clear)
        container.addView(bar)
        val scroll = ScrollView(this).apply { isFillViewport = true }
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        container.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        container.addView(label("Məlumatlar serverə göndərilmir. Son 50 SMS yalnız cihazda saxlanılır.", 12f, muted).apply {
            setPadding(0, dp(12), 0, 0)
        })
        setContentView(container)
        updatePermissionStatus()
        refreshInbox()
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(refresher)
        handler.post(refresher)
    }

    override fun onPause() {
        handler.removeCallbacks(refresher)
        super.onPause()
    }

    private fun hasSmsPermission(): Boolean =
        checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED

    private fun askPermission() {
        if (!hasSmsPermission()) requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 100)
        else updatePermissionStatus()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updatePermissionStatus()
    }

    private fun updatePermissionStatus() {
        val permitted = hasSmsPermission()
        status.text = if (permitted) "✓ SMS icazəsi aktivdir. Yeni SMS gözlənilir." else "SMS icazəsi verilməyib. Bildirişləri görmək üçün icazə ver."
        permissionButton.visibility = if (permitted) View.GONE else View.VISIBLE
    }

    private fun refreshInbox() {
        updatePermissionStatus()
        val items = SmsStore.all(this)
        list.removeAllViews()
        if (items.length() == 0) {
            list.addView(label("Hələ SMS yoxdur. İcazədən sonra gələn yeni SMS-lər burada görünəcək.", 15f, muted).apply {
                setPadding(dp(12), dp(25), dp(12), dp(25))
            })
            return
        }
        val fmt = SimpleDateFormat("dd.MM.yyyy  HH:mm", Locale.getDefault())
        for (i in 0 until items.length()) {
            val item = items.optJSONObject(i) ?: continue
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(15), dp(14), dp(15), dp(14))
                background = rounded(panel)
            }
            card.addView(label(item.optString("sender", "Naməlum"), 16f, white, true))
            card.addView(label(item.optString("body", ""), 15f, white).apply {
                setPadding(0, dp(7), 0, dp(7))
            })
            card.addView(label(fmt.format(Date(item.optLong("time", 0))), 12f, muted))
            list.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
        }
    }

    private fun rounded(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(15).toFloat()
    }

    private fun label(value: String, size: Float, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun dp(value: Int): Int = (resources.displayMetrics.density * value).toInt()
}
