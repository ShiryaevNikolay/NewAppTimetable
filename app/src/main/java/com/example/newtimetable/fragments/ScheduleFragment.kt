package com.example.newtimetable.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.fragment_schedule.view.*

class ScheduleFragment : AbstractTabFragment(), ItemTouchHelperLestener, DialogDeleteListener {
    private lateinit var daySchedule: String
    private var itemId: Int? = null
    private var clockStart: String? = null
    private var hoursStart: Int? = null
    private var minutesStart: Int? = null
    private var clockEnd: String? = null
    private var hoursEnd: Int? = null
    private var minutesEnd: Int? = null
    private var lesson: String? = null
    private var teacher: String? = null
    private var nameClass: String? = null
    private var week: String? = null
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
        var flag = true
        if (cursor.moveToFirst()) {
            do {
                // проверяем, совпадает ли день недели в базе данных с днём, куда добавляем данные
                if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_DAY)) == daySchedule) {
                    flag = false
                    itemId = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_ID))
                    clockStart = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_CLOCK_START))
                    hoursStart = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_HOURS_START))
                    minutesStart = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_MINUTES_START))
                    clockEnd = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_CLOCK_END))
                    hoursEnd = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_HOURS_END))
                    minutesEnd = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_MINUTES_END))
                    lesson = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_LESSON))
                    teacher = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_TEACHER))
                    nameClass = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_CLASS))
                    week = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_WEEK))
                    sortList(hoursStart!!, minutesStart!!)
                }
            } while (cursor.moveToNext())
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        itemAdapter = ScheduleAdapter(listItem, RequestCode().REQUEST_CODE_SCHEDULE)
        recyclerView.adapter = itemAdapter

        view.tv_day_off_schedule_activity.isVisible = flag

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

    private fun sortList(hoursStart: Int, minutesStart: Int) {
        if (listItem.isNotEmpty()) {
            var flagLoopOne = false
            for (i in 0 until listItem.size) {
                if (flagLoopOne) break
                if (hoursStart == listItem[i].hoursStart) {
                    flagLoopOne = true
                    var flagLoopTwo = false
                    var indexI = 0
                    for (j in 0 until listItem.size) {
                        if (flagLoopTwo) break
                        if (hoursStart == listItem[j].hoursStart) {
                            indexI = j
                            if (minutesStart < listItem[j].minutesStart) {
                                flagLoopTwo = true
                                listItem.add(indexI, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                            } else if (minutesStart == listItem[j].minutesStart && week == "1") {
                                flagLoopTwo = true
                                listItem.add(indexI, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                            }
                        }
                    }
                    if (!flagLoopTwo) listItem.add(++indexI, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                } else if (hoursStart < listItem[i].hoursStart) {
                    flagLoopOne = true
                    listItem.add(i, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                }
            }
            if (!flagLoopOne) listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
        } else listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart, minutesStart, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
    }
}