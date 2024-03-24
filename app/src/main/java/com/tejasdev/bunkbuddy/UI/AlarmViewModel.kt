package com.tejasdev.bunkbuddy.UI

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.tejasdev.bunkbuddy.alarm.AlarmReceiver
import com.tejasdev.bunkbuddy.datamodel.Lecture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Long.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    @ApplicationContext application: Application
): AndroidViewModel(application) {

    private val app = application

    fun setAlarm(lecture: Lecture){
        val hour = getHourFromTime(lecture.startTime)
        val minute = getMinuteFromTime(lecture.startTime)
        val day = getDayOfWeek(lecture.dayNumber)
        val calender = Calendar.getInstance()
        calender.timeInMillis = System.currentTimeMillis()
        calender.apply {
            set(Calendar.DAY_OF_WEEK, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        while(calender.timeInMillis<=System.currentTimeMillis()){
            calender.add(Calendar.DAY_OF_YEAR, 7)
        }
        val perc = getAttendancePerc(lecture)
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(
            app,
            AlarmReceiver::class.java
        )
        intent.putExtra(AlarmReceiver.ID, lecture.pid)
        intent.putExtra(AlarmReceiver.SUBJECT, lecture.subject.name)
        intent.putExtra(AlarmReceiver.TIME, lecture.startTime)
        intent.putExtra(AlarmReceiver.TARGET_PERCENT, lecture.subject.requirement)
        intent.putExtra(AlarmReceiver.CURRENT_PERCENT, perc)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            lecture.pid,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmTime = max(calender.timeInMillis - 5*60*1000, System.currentTimeMillis())

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            if(alarmManager.canScheduleExactAlarms()){
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
            else{
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
        }
        else{
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
        }
        Log.w("notification", "set time: $alarmTime currentTime: ${System.currentTimeMillis()}")
    }

    fun cancelAlarm(lecture: Lecture){
        Log.w("notification", "${lecture.subject.name} ${lecture.startTime}")
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cancelIntent = Intent(app, AlarmReceiver::class.java)
        cancelIntent.putExtra(AlarmReceiver.ID, lecture.pid)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            app,
            lecture.pid,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(cancelPendingIntent)
        cancelPendingIntent.cancel()
    }

    private fun getDayOfWeek(day: Int): Int {
        val res = when (day) {
            0 -> Calendar.MONDAY
            1 -> Calendar.TUESDAY
            2 -> Calendar.WEDNESDAY
            3 -> Calendar.THURSDAY
            4 -> Calendar.FRIDAY
            5 -> Calendar.SATURDAY
            6 -> Calendar.SUNDAY
            else -> throw IllegalArgumentException("Invalid day of the week: $day")
        }
        return res
    }

    private fun getHourFromTime(time: String): Int{
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val date = dateFormat.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        Log.w("notification", "hour ${calendar.get(Calendar.HOUR_OF_DAY)}")
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    private fun getMinuteFromTime(time: String): Int{
        val dateFormat = SimpleDateFormat("hh:mm aa", Locale.US)
        val date = dateFormat.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        Log.w("notification", "minute ${calendar.get(Calendar.MINUTE)}")
        return calendar.get(Calendar.MINUTE)
    }

    private fun getAttendancePerc(lecture: Lecture): Double {
        return ((lecture.subject.attended.toDouble()).div(lecture.subject.attended.toDouble() + lecture.subject.missed.toDouble()))*100
    }

}