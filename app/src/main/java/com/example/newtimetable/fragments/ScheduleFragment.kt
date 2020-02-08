package com.example.newtimetable.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.AddScheduleActivity
import com.example.newtimetable.R
import com.example.newtimetable.RecyclerSchedule
import com.example.newtimetable.adapters.ScheduleAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.*
import com.example.newtimetable.modules.SwipeDragItemHelper
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.fragment_schedule.view.*

class ScheduleFragment : AbstractTabFragment(), ItemTouchHelperLestener, DialogDeleteListener, OnClickItemListener {
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
    private var listItemRemove: ArrayList<RecyclerSchedule> = ArrayList()
    private var positionItemRemove: ArrayList<Int> = ArrayList()
    private var requestCode: Int? = null

    fun getInstance(context: Context, position: Int, database: SQLiteDatabase, requestCode: Int): ScheduleFragment {
        val args = Bundle()
        val fragment = ScheduleFragment()
        fragment.database = database
        fragment.requestCode = requestCode
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
                    if (requestCode == RequestCode().REQUEST_CODE_MAIN) {
                        if (PreferenceManager.getDefaultSharedPreferences(activity).getString("number_0f_week", "1") != "1" &&
                            PreferenceManager.getDefaultSharedPreferences(activity).getString("this_week", "12") == cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(context).KEY_WEEK))) {
                            flag = false
                        } else if (PreferenceManager.getDefaultSharedPreferences(activity).getString("number_0f_week", "1") == "1") {
                            flag = false
                        }
                    } else if (requestCode == RequestCode().REQUEST_CODE_SCHEDULE) {
                        flag = false
                    }
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
                    sortListWeek()
                }
            } while (cursor.moveToNext())
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        itemAdapter = requestCode?.let { ScheduleAdapter(listItem, it, this) }!!
        recyclerView.adapter = itemAdapter

        view.tv_day_off_schedule_activity.isVisible = flag

        if (requestCode != RequestCode().REQUEST_CODE_MAIN) {
            val callback: SwipeDragItemHelper? = context?.let { SwipeDragItemHelper(this, it) }
            val itemTouchHelper = callback?.let { ItemTouchHelper(it) }
            itemTouchHelper?.attachToRecyclerView(recyclerView)
        }
        return view
    }

    override fun onItemDismiss(position: Int) {
        positionItemRemove.clear()
        listItemRemove.add(listItem[position])
        positionItemRemove.add(position)
        listItem.removeAt(position)
        itemAdapter.notifyItemRemoved(position)

        view?.tv_day_off_schedule_activity?.isVisible = itemAdapter.itemCount == 0

        val dialogDelete: DialogFragment = CustomDialog(this, position)
        fragmentManager?.let { dialogDelete.show(it, "deleteDialog") }
    }

    override fun onClickPositiveDialog() {
        for (i in 0 until listItemRemove.size) {
            database.delete(ScheduleDBHelper(context).TABLE_SCHEDULE, ScheduleDBHelper(context).KEY_ID + " = " + listItemRemove[i].itemId, null)
        }
        listItemRemove.clear()
        itemAdapter.notifyDataSetChanged()
        view?.tv_day_off_schedule_activity?.isVisible = itemAdapter.itemCount == 0
    }

    override fun onClickNegativeDialog(position: Int) {
        for (i in 0 until positionItemRemove.size) {
            listItem.add(positionItemRemove[i], listItemRemove[i])
            itemAdapter.notifyItemInserted(positionItemRemove[i])
        }
        listItemRemove.clear()
        positionItemRemove.clear()

        view?.tv_day_off_schedule_activity?.isVisible = itemAdapter.itemCount == 0
    }

    private fun sortListWeek() {
        if (listItem.isNotEmpty()) {
            var flagLoopOne = false
            for (i in 0 until listItem.size) {
                if (PreferenceManager.getDefaultSharedPreferences(activity).getString("number_0f_week", "1") != "1" && requestCode == RequestCode().REQUEST_CODE_MAIN) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getString("this_week", "1") != week && week != "12")
                        continue
                }
                if (flagLoopOne) break
                if (hoursStart == listItem[i].hoursStart) {
                    flagLoopOne = true
                    var flagLoopTwo = false
                    var indexI = 0
                    for (j in 0 until listItem.size) {
                        if (flagLoopTwo) break
                        if (hoursStart == listItem[j].hoursStart) {
                            indexI = j
                            if (minutesStart!! < listItem[j].minutesStart) {
                                flagLoopTwo = true
                                listItem.add(indexI, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!,
                                    hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                            }
                        }
                    }
                    if (!flagLoopTwo) listItem.add(++indexI, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                } else if (hoursStart!! < listItem[i].hoursStart) {
                    flagLoopOne = true
                    listItem.add(i, RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                }
            }
            if (!flagLoopOne) {
                if ((PreferenceManager.getDefaultSharedPreferences(activity).getString("this_week", "1") == week || week == "12") && requestCode == RequestCode().REQUEST_CODE_MAIN) {
                    listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                } else if (requestCode == RequestCode().REQUEST_CODE_SCHEDULE) {
                    listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
                }
            }
        } else if ((PreferenceManager.getDefaultSharedPreferences(activity).getString("this_week", "1") == week || week == "12") && requestCode == RequestCode().REQUEST_CODE_MAIN) {
            listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
        } else if (requestCode == RequestCode().REQUEST_CODE_SCHEDULE) {
            listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
        }
    }

    override fun onClickItemListener(position: Int) {
        if (requestCode != RequestCode().REQUEST_CODE_MAIN) {
            val intent = Intent(context, AddScheduleActivity::class.java)
            intent.putExtra("day", daySchedule)
            intent.putExtra("itemId", listItem[position].itemId)
            intent.putExtra("lesson", listItem[position].lesson)
            intent.putExtra("teacher", listItem[position].teacher)
            intent.putExtra("nameClass", listItem[position].nameClass)
            intent.putExtra("clockStart", listItem[position].clockStart)
            intent.putExtra("clockEnd", listItem[position].clockEnd)
            intent.putExtra("hoursStart", listItem[position].hoursStart)
            intent.putExtra("hoursEnd", listItem[position].hoursEnd)
            intent.putExtra("minutesStart", listItem[position].minutesStart)
            intent.putExtra("minutesEnd", listItem[position].minutesEnd)
            intent.putExtra("week", listItem[position].week)
            intent.putExtra("requestCode", RequestCode().REQUEST_CODE_SCHEDULE_CHANGE)
            startActivityForResult(intent, RequestCode().REQUEST_CODE_SCHEDULE_CHANGE)
        }
    }
}