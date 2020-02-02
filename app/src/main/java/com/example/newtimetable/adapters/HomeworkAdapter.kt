package com.example.newtimetable.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerHomework
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.interfaces.OnLongClickItemListener
import kotlinx.android.synthetic.main.recycler_homework.view.*

class HomeworkAdapter(
    var listItem: ArrayList<RecyclerHomework>,
    var onClickItemListener: OnClickItemListener,
    var onLongClickItemListener: OnLongClickItemListener
) : RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        context = parent.context
        return HomeworkViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_homework, parent, false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        val itemList: RecyclerHomework = listItem[position]
        holder.itemView.task_item_rv_homework.text = itemList.task
        holder.itemView.tv_add_date_item_homework.text = context.resources.getString(R.string.homework_add_date) + " " + itemList.textAddData
        holder.itemView.tv_to_date_item_homework.text = context.resources.getString(R.string.homework_to_date) + " " + itemList.textToData
        var flag = false
        for (i in 0 until listItem.size) {
            if (listItem[i].checkBox) {
                flag = true
                break
            }
        }
        if (flag) holder.itemView.checkBox.visibility = View.VISIBLE else holder.itemView.checkBox.visibility = View.INVISIBLE
        holder.itemView.checkBox.isChecked = itemList.checkBox
        holder.itemView.setOnClickListener {
            if (holder.itemView.checkBox.isVisible) {
                if (holder.itemView.checkBox.isChecked) {
                    holder.itemView.checkBox.isChecked = false
                    onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, false)
                } else {
                    holder.itemView.checkBox.isChecked = true
                    onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, true)
                }
            } else onClickItemListener.onClickItemListener(holder.adapterPosition)
        }
        holder.itemView.setOnLongClickListener {
            if (holder.itemView.checkBox.isChecked) {
                holder.itemView.checkBox.isChecked = false
                holder.itemView.checkBox.isVisible = true
                onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, false)
            } else {
                holder.itemView.checkBox.isChecked = true
                holder.itemView.checkBox.isVisible = false
                onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, true)
            }
            return@setOnLongClickListener true
        }
        holder.itemView.checkBox.setOnClickListener {
            if (holder.itemView.checkBox.isChecked) {
                holder.itemView.checkBox.isChecked = true
                onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, true)
            } else {
                holder.itemView.checkBox.isChecked = false
                onLongClickItemListener.onLongClickItemListener(holder.adapterPosition, false)
            }
        }
    }

    inner class HomeworkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}