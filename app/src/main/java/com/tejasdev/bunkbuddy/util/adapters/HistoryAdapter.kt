package com.tejasdev.bunkbuddy.util.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tejasdev.bunkbuddy.R
import com.tejasdev.bunkbuddy.datamodel.HistoryItem

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var list: List<HistoryItem> = listOf()
    class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val messageTv = view.findViewById<TextView>(R.id.message)
        val timeStampTv = view.findViewById<TextView>(R.id.timestamp)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val endView = view.findViewById<View>(R.id.endView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.history_item_view, null)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = list[position]
        holder.apply {
            if(position==list.size-1) endView.visibility = View.INVISIBLE
            messageTv.text = item.message
            timeStampTv.text = String.format(itemView.context.getString(R.string.timeStamp), item.date, item.time)
            icon.setImageDrawable(getDrawableFromActionType(this.itemView.context ,item.actionType))
        }
    }

    private fun getDrawableFromActionType(context: Context, type: Int): Drawable{
        return when(type){
            0-> context.resources.getDrawable(R.drawable.ic_delete_history)
            1-> context.resources.getDrawable(R.drawable.ic_add_history)
            2-> context.resources.getDrawable(R.drawable.ic_add_history)
            3-> context.resources.getDrawable(R.drawable.ic_delete_history)
            4-> context.resources.getDrawable(R.drawable.ic_increase_history)
            5-> context.resources.getDrawable(R.drawable.ic_decrese_history)
            6-> context.resources.getDrawable(R.drawable.ic_increase_history)
            7-> context.resources.getDrawable(R.drawable.ic_decrese_history)
            8-> context.resources.getDrawable(R.drawable.ic_alerts_on)
            else -> context.resources.getDrawable(R.drawable.ic_alerts_off)
        }
    }
    fun setData(newList: List<HistoryItem>){
        list = newList
        notifyDataSetChanged()
    }
}