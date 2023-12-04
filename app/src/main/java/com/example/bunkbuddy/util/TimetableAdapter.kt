package com.example.bunkbuddy.util

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.R
import com.example.bunkbuddy.datamodel.Lecture
import kotlin.math.ceil
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup.LayoutParams

class TimetableAdapter(val context: Context): RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>(){

    private var list: List<Lecture> = arrayListOf()
    private var isFirstText = true

    class TimetableViewHolder(view: View): RecyclerView.ViewHolder(view){
        val subjectNameTv: TextView = view.findViewById(R.id.subject_name_tv)
        val requirementTv: TextView = view.findViewById(R.id.tt_requirement_tv)
        val remarksTv: TextView = view.findViewById(R.id.remarks_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        return TimetableViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.timetable_item_view, null)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        holder.itemView.minimumWidth=LayoutParams.MATCH_PARENT
        val lecture = list[position]
        holder.apply {
            subjectNameTv.text = lecture.subject.name
            requirementTv.text = "${lecture.startTime} to ${lecture.endTime}"

            val attended = lecture.subject.attended.toDouble()
            val missed = lecture.subject.missed.toDouble()
            val perc = ceil((attended).div(attended + missed).times(100)).toInt()

            if(perc>lecture.subject.requirement) {
                remarksTv.setTextColor(context.resources.getColor(R.color.primary_blue))
                if(isFirstText){
                    remarksTv.text = "Can Skip"
                }
                else{
                    remarksTv.text = "${perc}%"
                }
            }
            else{
                remarksTv.setTextColor(context.resources.getColor(R.color.red))
                if(isFirstText){
                    remarksTv.text = "Cannot Skip"
                }
                else{
                    remarksTv.text = "${perc}%"
                }
            }
        }
    }

    fun changeText(type: Int){
        isFirstText = (type==0)
        notifyDataSetChanged()
    }
    fun setData(newList: List<Lecture>){
        list = newList
        notifyDataSetChanged()
    }

}