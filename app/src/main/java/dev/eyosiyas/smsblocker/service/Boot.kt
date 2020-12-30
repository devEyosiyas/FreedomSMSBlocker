package dev.eyosiyas.smsblocker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class Boot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED == intent.action)) {
            val serviceIntent = Intent(context, BlockerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent) else context.startService(serviceIntent)
        }
    }
}