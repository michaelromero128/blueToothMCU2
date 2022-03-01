package com.example.simpletodo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskItemAdapter(val listOfItems: List<String>, val longClickListener: OnLongClickListener) : RecyclerView.Adapter<TaskItemAdapter.ViewHolder>() {

    interface OnLongClickListener {
        fun onItemLongClicked(position: Int)
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextView
        init{
            textView = itemView.findViewById(android.R.id.text1)
            textView.setOnLongClickListener {
                longClickListener.onItemLongClicked(adapterPosition)
                true
            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val context = p0.context
        val inflater = LayoutInflater.from(context)
        val taskItemView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, p0, false)
        return ViewHolder(taskItemView)

    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item: String = listOfItems.get(p1)
        p0.textView.text = item
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

}