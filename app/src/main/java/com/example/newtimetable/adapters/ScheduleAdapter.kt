package com.example.newtimetable.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerSchedule
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.recycler_schedule.view.*

class ScheduleAdapter(
    private var listItem: ArrayList<RecyclerSchedule>,
    private val fromActivity: Int,
    private var onClickItemListener: OnClickItemListener
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

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
        if (fromActivity == RequestCode().REQUEST_CODE_SCHEDULE) {
            if (itemList.week == "1") {
                holder.itemView.indicator_week.background = ContextCompat.getDrawable(context, R.color.colorAccent)
            } else if (itemList.week == "2") {
                holder.itemView.indicator_week.background = ContextCompat.getDrawable(context, R.color.colorRed)
            }
        }
        holder.itemView.clock_start_rv_schedule.text = itemList.clockStart
        if (itemList.clockEnd == "") {
            holder.itemView.clock_end_rv_schedule.isVisible = false
            holder.itemView.period_time_rv_schedule.isVisible = false
        }
        holder.itemView.clock_end_rv_schedule.text = itemList.clockEnd
        holder.itemView.lesson_rv_schedule.text = itemList.lesson
        if (itemList.teacher == "") {
            holder.itemView.teacher_rv_schedule.isVisible = false
        }
        holder.itemView.teacher_rv_schedule.text = itemList.teacher
        if (itemList.nameClass == "") {
            holder.itemView.class_rv_schedule.isVisible = false
        }
        holder.itemView.class_rv_schedule.text = itemList.nameClass

        holder.itemView.setOnClickListener {
            onClickItemListener.onClickItemListener(holder.adapterPosition)
        }
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}