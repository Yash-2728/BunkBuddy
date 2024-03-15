package com.tejasdev.bunkbuddy.util.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.Subject
import com.tejasdev.bunkbuddy.util.listeners.SubjectItemClickListener
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class SubjectAdapter(
    private val context: Context,
    private val listener: SubjectItemClickListener
    ): RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    private var list: ArrayList<Subject> = arrayListOf()

    class SubjectViewHolder(view: View):RecyclerView.ViewHolder(view){
        val nameTv: TextView = view.findViewById(R.id.subjectName)
        val progress: CircularProgressIndicator = view.findViewById(R.id.progress_bar)
        val attendedClassTv: TextView = view.findViewById(R.id.attended_tv)
        val remarks: TextView = view.findViewById(R.id.remarks_tv)
        val missedClasTv: TextView = view.findViewById(R.id.missed_tv)
        val requirementTv: TextView = view.findViewById(R.id.requirement_tv)
        val totalTv: TextView = view.findViewById(R.id.total_tv)

        val incAttendanceBtn: CardView = view.findViewById(R.id.inc_attendance_btn)
        val decAttendanceBtn: CardView = view.findViewById(R.id.dec_attendance_btn)
        val incMissedBtn: CardView = view.findViewById(R.id.inc_missed_btn)
        val decMissedBtn: CardView = view.findViewById(R.id.dec_missed_btn)

        val attendanceTv: TextView = view.findViewById(R.id.percent_tv)
        val incDecLl: LinearLayout = view.findViewById(R.id.ll_with_btn)
        val optionsBtn: ShapeableImageView = view.findViewById(R.id.options_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.subject_card_view, null)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val item = list[position]

        holder.optionsBtn.setOnClickListener {
            showPopupMenu(
                holder.itemView.context,
                it,
                item,
                position
            )
        }

        holder.incAttendanceBtn.setOnClickListener {
            listener.onIncreaseAttendanceBtnClicked(item)
        }
        holder.decAttendanceBtn.setOnClickListener {
            if(item.missed+item.attended>1) listener.onDecreaseAttendanceBtnClicked(item)
        }

        holder.decMissedBtn.setOnClickListener {
            if(item.missed+item.attended>1) listener.onDecreaseMissedBtnClicked(item)
        }

        holder.incMissedBtn.setOnClickListener {
            listener.onIncreaseMissedBtnClicked(item)
        }

        holder.itemView.setOnClickListener {
            if(holder.incDecLl.visibility == View.GONE) holder.incDecLl.visibility = View.VISIBLE
            else holder.incDecLl.visibility = View.GONE
        }
        holder.apply {
            nameTv.text = item.name
            missedClasTv.text = "Missed ${item.missed}"
            attendedClassTv.text ="Attended ${item.attended}"
            requirementTv.text = "Requirement: ${item.requirement}%"
            attendedClassTv.visibility = View.VISIBLE
            totalTv.visibility = View.VISIBLE

            val missed = item.missed.toDouble()
            val attended = item.attended.toDouble()
            val total = missed+attended
            totalTv.text = "Total ${(attended+missed).toInt()}"
            val missedPerc = (missed/total).times(100).roundToInt()
            val attendedPerc = 100 - missedPerc
            val requirement = item.requirement

            attendanceTv.text = "${attendedPerc}%"

            progress.setProgress(attendedPerc, true)
            var remarksText=""

            if(attendedPerc>=requirement){
                val canMiss = floor(((100-requirement).times(attended) - requirement.times(missed)).div(requirement)).toInt()
                remarks.setTextColor(context.resources.getColor(R.color.blue))
                remarksText = if(canMiss>1) "Can miss $canMiss classes in a row"
                else "Can miss $canMiss class"
                remarks.text = remarksText
            }
            else{
                val shouldAttend = ceil(((requirement-100).times(attended) + requirement.times(missed)).div(100-requirement)).toInt()
                remarks.setTextColor(context.resources.getColor(R.color.red))
                remarksText = if(shouldAttend>1) "Must attend $shouldAttend classes in a row"
                else "Must attend $shouldAttend class"
                remarks.text = remarksText
            }
        }
    }
    private fun showPopupMenu(context: Context, view: View, subject: Subject, pos: Int){
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.subject_popup_options)
        popupMenu.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.edit -> {
                    listener.onEditOptionSelected(subject)
                    true
                }
                else -> {
                    listener.onDeleteBtnClicked(pos, subject)
                    true
                }
            }
        }
        popupMenu.show()
    }
    fun addItem(subject: Subject, pos: Int){
        list.add(pos, subject)
        notifyDataSetChanged()
    }
    fun deleteAt(pos: Int){
        Log.w("delete-issue", "list: ${list.size} pos: $pos")
        list.removeAt(pos)
        notifyDataSetChanged()
    }
    fun getAtPostion(pos: Int): Subject {
        return list[pos]
    }
    fun swap(source: Int, dest: Int){
        Collections.swap(list, source, dest)
        notifyDataSetChanged()
    }

    fun setData(newList: List<Subject>){
        list = ArrayList(newList)
        notifyDataSetChanged()
    }
}