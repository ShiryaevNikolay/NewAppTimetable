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
import kotlinx.android.synthetic.main.recycler_schedule.view.*

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
        holder.itemView.clock_rv_schedule.text = itemList.clock
        holder.itemView.lesson_rv_schedule.text = itemList.lesson
        holder.itemView.teacher_rv_schedule.text = context.resources.getString(R.string.schedule_adapter_teacher) + " " + itemList.teacher
        holder.itemView.class_rv_schedule.text = context.resources.getString(R.string.schedule_adapter_class) + " " + itemList.nameClass
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}