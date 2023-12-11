package com.tejasdev.bunkbuddy.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.Subject

class SubjectDropdownAdapter(
    context: Context,
    list: List<Subject>
): ArrayAdapter<Subject>(context, R.layout.dropdown_item, list){

    private val subjects: MutableList<Subject> = ArrayList(list)

    private val inflater= LayoutInflater.from(context)


    override fun getCount(): Int {
        return subjects.size
    }

    override fun getItem(position: Int): Subject {
        return subjects[position]
    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var view = convertView
//        if(view==null){
//            view = inflater.inflate(R.layout.dropdown_item, parent, false)
//        }
//        val subject = getItem(position)
//
//        val tv = view!!.findViewById<View>(R.id.autoCompleteTextView) as TextView
//        tv.text = subject.name
//
//        return view
//    }

}