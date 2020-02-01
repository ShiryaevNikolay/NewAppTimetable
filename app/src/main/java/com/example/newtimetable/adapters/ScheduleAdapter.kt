package com.example.newtimetable.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerSchedule

class ScheduleAdapter(private var listItem: ArrayList<RecyclerSchedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        context = parent.context
        return ScheduleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_schedule, parent, false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val itemList = listItem[position]
        holder.clock.text = itemList.clock
        holder.lesson.text = itemList.lesson
        holder.teacher.text = context.resources.getString(R.string.schedule_adapter_teacher) + " " + itemList.teacher
        holder.nameClass.text = context.resources.getString(R.string.schedule_adapter_class) + " " + itemList.nameClass
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clock: TextView = itemView.findViewById(R.id.clock_rv_schedule)
        val lesson: TextView = itemView.findViewById(R.id.lesson_rv_schedule)
        val teacher: TextView = itemView.findViewById(R.id.teacher_rv_schedule)
        val nameClass: TextView = itemView.findViewById(R.id.class_rv_schedule)
    }
}