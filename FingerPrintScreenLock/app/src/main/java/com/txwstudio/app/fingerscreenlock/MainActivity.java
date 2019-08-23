package com.txwstudio.app.fingerscreenlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int OriginalScreenTimeout, OriginalScreenBrightness;
    Boolean getPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOriginalSettings();
        if (Settings.System.canWrite(this)) {
            getPermission = false;
            lockScreen();
        } else {
            Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show();
            getPermission = true;
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        restoreFromLockScreen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        restoreFromLockScreen();
        if (!getPermission) {
        finishAndRemoveTask();
        }
    }


    private void hideSystemUI() {
        getWindow().addFlags(16);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void getOriginalSettings() {
        this.OriginalScreenTimeout = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 120000);
        this.OriginalScreenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 63);
        Log.i("TEST", "OriginalScreenTimeout: " + OriginalScreenTimeout);
        Log.i("TEST", "OriginalScreenBrightness: " + OriginalScreenBrightness);
    }

    private void lockScreen() {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 0);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 10);
    }

    private void restoreFromLockScreen() {
        if (!getPermission) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, OriginalScreenTimeout);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, OriginalScreenBrightness);
        }
    }




}
