package com.tejasdev.bunkbuddy

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.activities.MainActivity
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase
import java.util.Calendar

class NewAppWidget : AppWidgetProvider() {

    private lateinit var subjectRepository: SubjectRepository
    private lateinit var allLectureLive: LiveData<List<Lecture>>
    private var allLectureSync: ArrayList<Lecture> = arrayListOf()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val db = SubjectDatabase.getDatabase(context)
        subjectRepository = SubjectRepository(db)
        allLectureLive = getLectureForToday(subjectRepository)

        allLectureLive.observeForever {
            if(it!=null) updateList(context, it)
        }

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
            remoteViews.setRemoteAdapter(R.id.timetable_list, serviceIntent)
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            remoteViews.setEmptyView(R.id.timetable_list, R.id.timetable_list)
            remoteViews.setPendingIntentTemplate(
                R.id.timetable_list, addPendingIntent
            )
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    private fun updateList(context: Context?, newList: List<Lecture>) {
        allLectureSync.clear()
        allLectureSync.addAll(newList)
        notifyAppWidgetViewDataChanged(context!!)
    }

    private fun getLectureForToday(subjectRepository: SubjectRepository): LiveData<List<Lecture>> {
        val calender = Calendar.getInstance()
        val day = calender.get(Calendar.DAY_OF_WEEK)
        return when(day){
            1 -> subjectRepository.sunday
            2 -> subjectRepository.monday
            3 -> subjectRepository.tuesday
            4 -> subjectRepository.wednesday
            5 -> subjectRepository.thursday
            6 -> subjectRepository.friday
            else -> subjectRepository.sunday
        }
    }

    private fun notifyAppWidgetViewDataChanged(context: Context) {

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
    override fun onEnabled(context: Context) {
        notifyAppWidgetViewDataChanged(context)
    }

    override fun onDisabled(context: Context) {
        notifyAppWidgetViewDataChanged(context)
    }

    override fun onReceive(context: Context?, intent: Intent?) {

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