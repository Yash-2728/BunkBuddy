package com.tejasdev.bunkbuddy

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase
import java.util.Calendar

class WidgetService: RemoteViewsService() {

    private lateinit var repo: SubjectRepository
    private lateinit var lectures: LiveData<List<Lecture>>
    private var lectureListSync: ArrayList<Lecture> = arrayListOf()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return WidgetItemFactory(applicationContext)
    }

    inner class WidgetItemFactory(private val context: Context): RemoteViewsFactory {

        override fun onCreate() {
            val db = SubjectDatabase.getDatabase(context)
            repo = SubjectRepository(db)
            lectures = getLectureForToday(repo)
            lectures.observeForever{
                updateList(it)
            }
        }

        override fun onDataSetChanged() {
            if(lectureListSync.isNotEmpty()) getViewAt(0)
        }

        override fun onDestroy() {
            //I dont know what to do here
        }

        override fun getCount(): Int {
            return lectureListSync.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val item = lectureListSync[position]

            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.widget_item_view
            )
            remoteViews.setTextViewText(R.id.subjectNameTv, item.subject.name)
            remoteViews.setTextViewText(R.id.startTimeTv, item.startTime)
            return remoteViews
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        private fun updateList(list: List<Lecture>){
            lectureListSync.clear()
            lectureListSync.addAll(list)
            onDataSetChanged()
        }

        private fun getLectureForToday(repo: SubjectRepository): LiveData<List<Lecture>> {
            val calender = Calendar.getInstance()
            return when(calender.get(Calendar.DAY_OF_WEEK)){
                1 -> repo.sunday
                2 -> repo.monday
                3 -> repo.tuesday
                4 -> repo.wednesday
                5 -> repo.thursday
                6 -> repo.friday
                else -> repo.sunday
            }
        }

    }

}