package com.tejasdev.bunkbuddy.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.LiveData
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.Lecture
import com.tejasdev.bunkbuddy.repository.SubjectRepository
import com.tejasdev.bunkbuddy.room.SubjectDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
                if(it!=null) updateList(it)
            }
        }

        override fun onDataSetChanged() {
            if(lectureListSync.isNotEmpty()) getViewAt(0)
        }

        override fun onDestroy() {}

        override fun getCount(): Int {
            if(lectureListSync.size==0) return 1
            return lectureListSync.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            if(lectureListSync.size==0) {
                return RemoteViews(
                    context.packageName,
                    R.layout.empty_widget_placeholder
                )
            }
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
            var count = 0
            list.forEach { lecture->
                if(ifLectureLater(lecture)) {
                    val index = search(lecture.startTime)
                    lectureListSync.add(index+1, lecture)
                    count++
                }
            }
            Log.w("widget-flow", "widget service: $count")
            onDataSetChanged()
        }
        private fun search(time: String): Int{
            val timeInMin = timeInMinutes(time)
            var low = 0
            var high = lectureListSync.size-1
            var ans = -1
            while(low<=high){
                val mid = (low+high)/2
                val midTime = timeInMinutes(lectureListSync[mid].startTime)
                if(midTime<=timeInMin){
                    ans = mid
                    low = mid+1
                }
                else high = mid-1
            }
            return ans
        }
        private fun ifLectureLater(lecture: Lecture): Boolean{
            val timeInMilis = Calendar.getInstance().time
            val lecture24H = timeInMinutes(lecture.startTime)
            val current24H = timeInMinutes(SimpleDateFormat("hh:mm a", Locale.US).format(timeInMilis))
            return lecture24H>=current24H
        }

        private fun timeInMinutes(time: String): Int{
            val timeArray = time.split(":", " ")
            val amPM = if(timeArray[2]=="PM"){
                if(timeArray[0].toInt()<12) 12*60
                else 0
            }
            else {
                if(timeArray[0].toInt()==12) -12*60
                else 0
            }
            return timeArray[0].toInt()*60 + timeArray[1].toInt() + amPM
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
                else -> repo.saturday
            }
        }

    }

}