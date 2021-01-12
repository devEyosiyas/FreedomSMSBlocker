package dev.eyosiyas.smsblocker.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.view.MainActivity


class InformManager constructor(base: Context) : ContextWrapper(base) {
    val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val defaultUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.setSound(defaultUri, audioAttributes)
        channel.enableVibration(true)
        channel.lightColor = R.color.colorAccent
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager.createNotificationChannel(channel)
    }

    fun showNotification(title: String?, message: String?): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP), 0))
    }

    companion object {
        private const val CHANNEL_ID: String = "0969137138"
        private const val CHANNEL_NAME: String = "Freedom SMS Blocker"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
    }
}