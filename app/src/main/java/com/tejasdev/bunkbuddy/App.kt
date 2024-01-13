package com.tejasdev.bunkbuddy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                AlarmReceiver.NOTIFICATION_CHANNEL_ID,
                "BunkBuddy",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for lecture notifications"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}