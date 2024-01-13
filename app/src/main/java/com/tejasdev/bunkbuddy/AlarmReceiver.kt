package com.tejasdev.bunkbuddy

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tejasdev.bunkbuddy.activities.MainActivity
import java.lang.Long

class AlarmReceiver: BroadcastReceiver() {

    private fun showNotification(context: Context, subject: String, time: String, id: Int, perc: Int, target: Int){
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Class Alert: $subject at $time")
            .setContentText("Your attendance is ${toPerc(perc)}. Attend this class to maintain progress towards your ${toPerc(target)} target.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_login)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(id, builder.build())
            }
        }
    }

    private fun createNotificationChannel(context: Context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            val manager = context.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.w("notification", "received")
        intent?.let{
            val subject = it.getStringExtra(SUBJECT)?:""
            val time = it.getStringExtra(TIME)?:""
            val id = it.getIntExtra(ID, -1)
            val perc = it.getIntExtra(CURRENT_PERCENT, 32)
            val target = it.getIntExtra(TARGET_PERCENT, 75)
            showNotification(context!!, subject, time, id, perc, target)
            createNextAlarm(subject, id, perc, target, context, time)
        }
    }

    private fun createNextAlarm(subject: String, id: Int, perc: Int, target: Int, context: Context, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(
            context,
            AlarmReceiver::class.java
        )
        intent.putExtra(ID, id)
        intent.putExtra(SUBJECT, subject)
        intent.putExtra(TIME, time)
        intent.putExtra(TARGET_PERCENT, target)
        intent.putExtra(CURRENT_PERCENT, perc)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            if(alarmManager.canScheduleExactAlarms()){
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY*7,
                    pendingIntent
                )
            }
            else{
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY*7,
                    pendingIntent
                )
            }
        }
        else{
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmManager.INTERVAL_DAY*7,
                pendingIntent
            )
        }
        Log.w("notification", "re alarmTime: ${System.currentTimeMillis() + AlarmManager.INTERVAL_DAY*7} current: ${System.currentTimeMillis()}")
    }

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_CHANNEL_NAME = "BunkBuddy Notifications"
        const val SUBJECT = "subject"
        const val TIME = "time"
        const val ID = "id"
        const val CURRENT_PERCENT = "current_percent"
        const val TARGET_PERCENT = "target_percent"
    }

    private fun toPerc(value: Int): String = "$value%"
}