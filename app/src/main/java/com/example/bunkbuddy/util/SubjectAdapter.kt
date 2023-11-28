package com.example.bunkbuddy.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bunkbuddy.R
import com.example.bunkbuddy.datamodel.Subject
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlin.math.roundToInt

class SubjectAdapter(val context: Context): RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    private var list: ArrayList<Subject> = arrayListOf()

    class SubjectViewHolder(view: View):RecyclerView.ViewHolder(view){
        val nameTv = view.findViewById<TextView>(R.id.subjectName)
        val progress = view.findViewById<LinearProgressIndicator>(R.id.subjectProgress)
        val lastUpdatedTv = view.findViewById<TextView>(R.id.lastUpdatedTxt)
        val attendedPercTv = view.findViewById<TextView>(R.id.attendedPercTv)
        val missedPercTv = view.findViewById<TextView>(R.id.missedPercTv)
        val attendedClassTv = view.findViewById<TextView>(R.id.attendedClassTv)
        val missedClasTv = view.findViewById<TextView>(R.id.missedClassTv)
        val remarks = view.findViewById<TextView>(R.id.remarksTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.subject_card_view, parent))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val item = list[position]

        holder.apply {
            nameTv.text = item.name
            missedClasTv.text = item.missed.toString()
            attendedClassTv.text = item.attended.toString()

            val missed = item.missed.toDouble()
            val attended = item.attended.toDouble()
            val total = missed+attended
            val missedPerc = (missed/total).times(100).roundToInt()
            val attendedPerc = 100 - missedPerc

            progress.setProgress(attendedPerc, true)
            attendedPercTv.text = attendedPerc.toString()
            missedPercTv.text = missedPercTv.toString()

            lastUpdatedTv.text = item.lastUpdated

            if(attendedPerc>=75){
                val canMiss = (attended - missed.times(3)).div(3)
                remarks.setTextColor(context.resources.getColor(R.color.blue))
                remarks.text = "You can miss $canMiss classes in a row."
            }
            else{
                val shouldAttend = missed.times(3) - attended
                remarks.setTextColor(context.resources.getColor(R.color.red))
                remarks.text = "You need to attend $shouldAttend classes in a row."
            }

        }
    }

    fun setData(newList: ArrayList<Subject>){
        list = newList
        notifyDataSetChanged()
    }
}