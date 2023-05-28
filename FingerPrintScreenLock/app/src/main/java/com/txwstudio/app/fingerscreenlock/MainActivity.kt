package com.txwstudio.app.fingerscreenlock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.txwstudio.app.fingerscreenlock.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var originalScreenTimeout = 0
    private var originalScreenBrightness = 0
    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // hideSystemUI()
        enterImmersiveMode()
    }

    override fun onResume() {
        super.onResume()
        getOriginalSettings()
        if (Settings.System.canWrite(this)) {
            isPermissionGranted = false
            lockScreen()
        } else {
            Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show()
            isPermissionGranted = true
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        restoreFromLockScreen()
    }

    override fun onStop() {
        super.onStop()
        restoreFromLockScreen()
        if (!isPermissionGranted) {
            exitProcess(0)
        }
    }

    private fun hideSystemUI() {
        window.addFlags(16)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun enterImmersiveMode() {
        val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    private fun getOriginalSettings() {
        originalScreenTimeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 120000)
        originalScreenBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 63)
        Log.i("TEST", "OriginalScreenTimeout: $originalScreenTimeout")
        Log.i("TEST", "OriginalScreenBrightness: $originalScreenBrightness")
    }

    private fun lockScreen() {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 0)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 10)
    }

    private fun restoreFromLockScreen() {
        if (!isPermissionGranted) {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, originalScreenTimeout)
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, originalScreenBrightness)
        }
    }

    companion object {
        private const val TAG = "MainActivity2"
    }
}