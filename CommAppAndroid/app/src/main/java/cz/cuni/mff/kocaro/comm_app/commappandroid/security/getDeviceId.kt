package cz.cuni.mff.kocaro.comm_app.commappandroid.security

import android.annotation.SuppressLint
import android.provider.Settings
import android.content.Context

/**
 * Helper function to get a provisional user identifier
 */
@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_device"
}