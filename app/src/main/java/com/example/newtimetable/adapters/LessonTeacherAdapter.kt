package com.example.newtimetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerItem
import com.example.newtimetable.interfaces.OnClickItemListener
import kotlinx.android.synthetic.main.recycler_item.view.*

class LessonTeacherAdapter(private var listItem: ArrayList<RecyclerItem>, private var onClickItemListener: OnClickItemListener) :
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
        if (itemList.type != "" && itemList.type != null)
            holder.itemView.tv_card_recycler.text =
                (holder.itemView.tv_card_recycler.text as String?)?.plus("(" + itemList.type + ")")
        holder.itemView.setOnClickListener {
            onClickItemListener.onClickItemListener(holder.adapterPosition)
        }
    }

    inner class LessonTeacherHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}