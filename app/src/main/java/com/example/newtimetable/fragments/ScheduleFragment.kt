package com.example.newtimetable.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerSchedule
import com.example.newtimetable.adapters.ScheduleAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.DialogDeleteListener
import com.example.newtimetable.interfaces.ItemTouchHelperLestener
import com.example.newtimetable.modules.SwipeDragItemHelper

class ScheduleFragment : AbstractTabFragment(), ItemTouchHelperLestener, DialogDeleteListener {
    private lateinit var daySchedule: String
    private var itemId: Int? = null
    private var clock: String? = null
    private var hours: Int? = null
    private var minutes: Int? = null
    private var lesson: String? = null
    private var teacher: String? = null
    private var nameClass: String? = null
    private lateinit var database: SQLiteDatabase
    private var listItem: ArrayList<RecyclerSchedule> = ArrayList()
    private lateinit var itemAdapter: ScheduleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var item: RecyclerSchedule

    fun getInstance(context: Context, position: Int, database: SQLiteDatabase): ScheduleFragment {
        val args = Bundle()
        val fragment = ScheduleFragment()
        fragment.database = database
        fragment.arguments = args
        when (position) {
            0 -> {
                context.getString(R.string.tab_title_mon).let { fragment.setTitle(it) }
                fragment.daySchedule = "mon"
            }
            1 -> {
                context.getString(R.string.tab_title_tues).let { fragment.setTitle(it) }
                fragment.daySchedule = "tues"
            }
            2 -> {
                context.getString(R.string.tab_title_wed).let { fragment.setTitle(it) }
                fragment.daySchedule = "wed"
            }
            3 -> {
                context.getString(R.string.tab_title_thurs).let { fragment.setTitle(it) }
                fragment.daySchedule = "thurs"
            }
            4 -> {
                context.getString(R.string.tab_title_fri).let { fragment.setTitle(it) }
                fragment.daySchedule = "fri"
            }
            5 -> {
                context.getString(R.string.tab_title_sat).let { fragment.setTitle(it) }
                fragment.daySchedule = "sat"
            }
        }
        return fragment
    }

    @SuppressLint("Recycle")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_schedule, container, false)

        val cursor: Cursor = database.query(ScheduleDBHelper(context).TABLE_SCHEDULE, null, null, null, null, null, null)
        listItem.clear()
        // добавляем в список данные (названия предметов) из базы данных
        if (cursor.moveToFirst()) {
            do {
                // проверяем, совпадает ли день недели в базе данных с днём, куда добавляем данные
                if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_DAY)) == daySchedule) {
                    itemId = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_ID))
                    clock = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_CLOCK))
                    hours = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_HOURS))
                    minutes = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_MINUTES))
                    lesson = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_LESSON))
                    teacher = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_TEACHER))
                    nameClass = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_CLASS))
                    sortList(hours!!, minutes!!)
                }
            } while (cursor.moveToNext())
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        itemAdapter = ScheduleAdapter(listItem)
        recyclerView.adapter = itemAdapter

        val callback: SwipeDragItemHelper? = context?.let { SwipeDragItemHelper(this, it) }
        val itemTouchHelper = callback?.let { ItemTouchHelper(it) }
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        return view
    }

    override fun onItemDismiss(position: Int) {
        item = listItem[position]
        listItem.removeAt(position)
        itemAdapter.notifyItemRemoved(position)

        val dialogDelete: DialogFragment = CustomDialog(this, position)
        fragmentManager?.let { dialogDelete.show(it, "deleteDialog") }
    }

    override fun onClickPositiveDialog() {
        database.delete(ScheduleDBHelper(context).TABLE_SCHEDULE, ScheduleDBHelper(context).KEY_ID + " = " + item.itemId, null)
    }

    override fun onClickNegativeDialog(position: Int) {
        listItem.add(position, item)
        itemAdapter.notifyItemInserted(position)
    }

    private fun sortList(hours: Int, minutes: Int) {
        if (listItem.isNotEmpty()) {
            var flagLoopOne = false
            for (i in 0 until listItem.size) {
                if (flagLoopOne) break
                if (hours == listItem[i].hours) {
                    flagLoopOne = true
                    var flagLoopTwo = false
                    var indexI = 0
                    for (j in 0 until listItem.size) {
                        if (flagLoopTwo) break
                        if (hours == listItem[j].hours) {
                            indexI = j
                            if (minutes < listItem[j].minutes) {
                                flagLoopTwo = true
                                listItem.add(indexI, RecyclerSchedule(itemId!!, clock!!, hours, minutes, lesson!!, teacher!!, nameClass!!))
                            }
                        }
                    }
                    if (!flagLoopTwo) listItem.add(++indexI, RecyclerSchedule(itemId!!, clock!!, hours, minutes, lesson!!, teacher!!, nameClass!!))
                } else if (hours < listItem[i].hours) {
                    flagLoopOne = true
                    listItem.add(i, RecyclerSchedule(itemId!!, clock!!, hours, minutes, lesson!!, teacher!!, nameClass!!))
                }
            }
            if (!flagLoopOne) listItem.add(RecyclerSchedule(itemId!!, clock!!, hours, minutes, lesson!!, teacher!!, nameClass!!))
        } else listItem.add(RecyclerSchedule(itemId!!, clock!!, hours, minutes, lesson!!, teacher!!, nameClass!!))
    }
}