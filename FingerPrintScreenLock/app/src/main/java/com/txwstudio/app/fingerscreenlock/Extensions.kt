package com.txwstudio.app.fingerscreenlock

import android.util.Log

fun Any.logV(tag: String, message: String) {
    Log.v(tag, message)
}

fun Any.logI(tag: String, message: String) {
    Log.i(tag, message)
}

fun Any.logW(tag: String, message: String) {
    Log.w(tag, message)
}

fun Any.logE(tag: String, message: String) {
    Log.e(tag, message)
}