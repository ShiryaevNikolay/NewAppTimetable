package com.example.newtimetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerItem

class LessonTeacherAdapter : RecyclerView.Adapter<LessonTeacherAdapter.LessonTeacherHolder>() {

    private lateinit var listItem: ArrayList<RecyclerItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonTeacherHolder {
        return LessonTeacherHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: LessonTeacherHolder, position: Int) {
        val itemList: RecyclerItem = listItem.get(position)
        holder.textView.text = itemList.text
    }

    inner class LessonTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_card_recycler)
    }
}