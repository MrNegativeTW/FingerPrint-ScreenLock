package com.txwstudio.app.fingerscreenlock

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.fingerscreenlock.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var originalTimeout = 0
    private var originalBrightness = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enterImmersiveMode()
    }

    override fun onResume() {
        super.onResume()
        if (isWriteSettingsPermissionGranted()) {
            logI(TAG, "Write settings permission granted!")
            binding.textViewLockMsg.visibility = View.VISIBLE
            getOriginalSettings()
            lockScreen()
        } else {
            showPermissionRequestDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isWriteSettingsPermissionGranted()) {
            rollbackToOriginalSettings()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isWriteSettingsPermissionGranted()) {
            rollbackToOriginalSettings()
            finishAndRemoveTask()
            exitProcess(0)
        }
    }

    /**
     * Decompiled from someone else's app, what does 16 mean? idk.
     */
    private fun hideSystemUI() {
        window.addFlags(16)
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private fun enterImmersiveMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    private fun isWriteSettingsPermissionGranted(): Boolean {
        return Settings.System.canWrite(this)
    }

    private fun showPermissionRequestDialog() {
        MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.mainAct_permissionDialog_title))
                .setMessage(resources.getString(R.string.mainAct_permissionDialog_message))
                .setPositiveButton(resources.getString(R.string.mainAct_permissionDialog_positiveBtn)) { dialog, _ ->
                    dialog.dismiss()
                    startActivity(
                            Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                    Uri.parse("package:$packageName"))
                    )
                }
                .setNegativeButton(resources.getString(R.string.mainAct_permissionDialog_negativeBtn)) { dialog, _ ->
                    exitProcess(0)
                }
                .setCancelable(false)
                .create()
                .show()
    }

    private fun getOriginalSettings() {
        originalTimeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, DEFAULT_TIMEOUT_2_MIN)
        originalBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, DEFAULT_BRIGHTNESS_HALF)
        logI(TAG, "Original timeout: $originalTimeout, brightness: $originalBrightness")
    }

    private fun lockScreen() {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, MINIMUM_TIMEOUT)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, MINIMUM_BRIGHTNESS)
    }

    private fun rollbackToOriginalSettings() {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, originalTimeout)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, originalBrightness)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val DEFAULT_TIMEOUT_2_MIN = 120000
        private const val DEFAULT_BRIGHTNESS_HALF = 50
        private const val MINIMUM_TIMEOUT = 0
        private const val MINIMUM_BRIGHTNESS = 10
    }
}