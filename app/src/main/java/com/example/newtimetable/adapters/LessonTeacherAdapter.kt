package com.example.newtimetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerItem
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.interfaces.OnLongClickItemListener
import kotlinx.android.synthetic.main.recycler_item.view.*
import kotlinx.android.synthetic.main.recycler_item.view.checkBox

class LessonTeacherAdapter(
    private var listItem: ArrayList<RecyclerItem>,
    private var onClickItemListener: OnClickItemListener,
    var onLongClickItemListener: OnLongClickItemListener
) :
    RecyclerView.Adapter<LessonTeacherAdapter.LessonTeacherHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonTeacherHolder {
        return LessonTeacherHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: LessonTeacherHolder, position: Int) {
        val itemList: RecyclerItem = listItem[position]
        holder.itemView.tv_card_recycler.text = itemList.text
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

    inner class LessonTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}