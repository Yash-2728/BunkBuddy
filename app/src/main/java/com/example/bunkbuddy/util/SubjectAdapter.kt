package com.example.bunkbuddy.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.R
import com.example.bunkbuddy.datamodel.Subject
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class SubjectAdapter(
    private val context: Context,
    private val listener: subjectItemClickListener
    ): RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    private var list: List<Subject> = listOf()

    class SubjectViewHolder(view: View):RecyclerView.ViewHolder(view){
        val nameTv: TextView = view.findViewById(R.id.subjectName)
        val progress: LinearProgressIndicator = view.findViewById(R.id.subjectProgress)
        val lastUpdatedTv: TextView = view.findViewById(R.id.lastUpdatedTxt)
        val attendedClassTv: TextView = view.findViewById(R.id.attendedClassTv)
        val remarks: TextView = view.findViewById(R.id.remarksTv)
        val missedClasTv: TextView = view.findViewById(R.id.missedClassTv)
        val requirementTv: TextView = view.findViewById(R.id.requirementTv)

        val incAttendenceBtn: CardView = view.findViewById(R.id.incAttendenceButton)
        val decAttendenceBtn: CardView = view.findViewById(R.id.decAttendenceButton)
        val incMissedBtn: CardView = view.findViewById(R.id.incMissedButton)
        val decMissedBtn: CardView = view.findViewById(R.id.decMissedButton)
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

        holder.incAttendenceBtn.setOnClickListener {
            listener.onIncreaseAttendenceBtnClicked(item)
        }
        holder.decAttendenceBtn.setOnClickListener {
            if(item.missed+item.attended>1) listener.onDecreaseAttendenceBtnClicked(item)
        }

        holder.decMissedBtn.setOnClickListener {
            if(item.missed+item.attended>1) listener.onDecreaseMissedBtnClicked(item)
        }

        holder.incMissedBtn.setOnClickListener {
            listener.onIncreaseMissedBtnClicked(item)
        }


        holder.apply {
            nameTv.text = item.name
            missedClasTv.text = item.missed.toString()
            attendedClassTv.text = item.attended.toString()
            requirementTv.text = "Requirement: ${item.requirement.toString()}%"

            val missed = item.missed.toDouble()
            val attended = item.attended.toDouble()
            val total = missed+attended
            val missedPerc = (missed/total).times(100).roundToInt()
            val attendedPerc = 100 - missedPerc
            val requirement = item.requirement

            progress.setProgress(attendedPerc, true)

            lastUpdatedTv.text = "Last updated on ${item.lastUpdated}"
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

    fun setData(newList: List<Subject>){
        list = newList
        notifyDataSetChanged()
    }
}