package cz.cuni.mff.kocaro.comm_app.commappandroid.security

import android.provider.Settings
import android.content.Context

fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_device"
}