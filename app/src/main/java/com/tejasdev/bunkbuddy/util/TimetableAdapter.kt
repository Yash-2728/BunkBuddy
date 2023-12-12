package com.tejasdev.bunkbuddy.util

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.Lecture
import kotlin.math.ceil
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup.LayoutParams
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import java.util.Collections

class TimetableAdapter(val context: Context): RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>(){

    private var list: ArrayList<Lecture> = arrayListOf()
    private var isFirstText = true

    class TimetableViewHolder(view: View): RecyclerView.ViewHolder(view){
        val subjectNameTv: TextView = view.findViewById(R.id.subject_name_tv)
        val requirementTv: TextView = view.findViewById(R.id.tt_requirement_tv)
        val remarksTv: TextView = view.findViewById(R.id.remarks_tv)
        val rootView: MaterialCardView = view.findViewById(R.id.root)
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
                rootView.strokeColor = context.resources.getColor(R.color.primary_blue)
                if(isFirstText){
                    remarksTv.text = "Can Skip"
                }
                else{
                    remarksTv.text = "${perc}%"
                }
            }
            else{
                rootView.strokeColor = context.resources.getColor(R.color.red)
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
    fun addItem(item: Lecture, pos: Int){
        list.add(pos, item)
        notifyDataSetChanged()
    }
    fun remove(source: Int){
        list.removeAt(source)
        notifyDataSetChanged()
    }
    fun swap(source: Int, dest: Int){
        Collections.swap(list, source, dest)
        notifyItemChanged(source, dest)
    }

    fun getAtPos(pos: Int): Lecture {
        return list[pos]
    }

    fun changeText(type: Int){
        isFirstText = (type==0)
        notifyDataSetChanged()
    }
    fun setData(newList: List<Lecture>){
        list = ArrayList(newList)
        notifyDataSetChanged()
    }

}