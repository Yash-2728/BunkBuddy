package com.tejasdev.bunkbuddy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase
import java.util.Calendar

class NewAppWidget : AppWidgetProvider() {

//    TODO("first check if it runs and then imrpovise")

    private lateinit var subjectRepository: SubjectRepository
    private lateinit var allLectureLive: LiveData<List<Lecture>>
    private lateinit var allLectureSync: ArrayList<Lecture>

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        Log.w("widget-flow", "1 onUpdate")
        // There may be multiple widgets active, so update all of them

        val db = SubjectDatabase.getDatabase(context)
        subjectRepository = SubjectRepository(db)
        allLectureLive = getLectureForToday(subjectRepository)
        
        allLectureSync = allLectureLive.value?.let { ArrayList(it) }!!

        for (appWidgetId in appWidgetIds) {
            val addIntent = Intent(context, javaClass)
            addIntent.action = actionUpdate

            val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                0 or PendingIntent.FLAG_MUTABLE
            }
            else { 0 }

            val addPendingIntent = PendingIntent.getBroadcast(
                context, 0, addIntent, flag
            )

            val serviceIntent = Intent(context, WidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            val intent = Intent(
                context, MainActivity::class.java
            )
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, flag
            )

            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.new_app_widget
            )
            remoteViews.setOnClickPendingIntent(R.id.timetable_list, pendingIntent)
            remoteViews.setRemoteAdapter(R.id.timetable_list, serviceIntent)

            //not sure what this does
            remoteViews.setEmptyView(R.id.timetable_list, R.id.timetable_list)
            remoteViews.setPendingIntentTemplate(
                R.id.timetable_list, addPendingIntent
            )
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun updateList(context: Context, list: List<Lecture>) {
        Log.w("widget-flow", "1 updateList")
        allLectureSync.clear()
        allLectureSync.addAll(list)
        notifyAppWidgetViewDataChanged(context)
    }

    private fun notifyAppWidgetViewDataChanged(context: Context) {
        Log.w("widget-flow", "notifyAppWidgetDataChanged")

        val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
        widgetManager.notifyAppWidgetViewDataChanged(
            widgetManager.getAppWidgetIds(
                context.applicationContext.packageName.let{
                    ComponentName(
                        it,
                        NewAppWidget::class.java.name
                    )
                }
            ),
            R.id.timetable_list
        )
    }

    private fun getLectureForToday(repo: SubjectRepository): LiveData<List<Lecture>> {
        Log.w("widget-flow", "getLectureForDay")

        val calender = Calendar.getInstance()
        return when(calender.get(Calendar.DAY_OF_WEEK)){
            1 -> subjectRepository.sunday
            2 -> subjectRepository.monday
            3 -> subjectRepository.tuesday
            4 -> subjectRepository.wednesday
            5 -> subjectRepository.thursday
            6 -> subjectRepository.friday
            else -> subjectRepository.sunday
        }
    }

    override fun onEnabled(context: Context) {
        Log.w("widget-flow", "onEnabled")
        notifyAppWidgetViewDataChanged(context)
    }

    override fun onDisabled(context: Context) {
        Log.w("widget-flow", "onDisabled")
        notifyAppWidgetViewDataChanged(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.w("widget-flow", "onReceive")

        super.onReceive(context, intent)
        if(intent==null) return
        notifyAppWidgetViewDataChanged(context!!)
    }

    companion object{
        const val actionUpdate = "UPDATE_LISTVIEW"
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}