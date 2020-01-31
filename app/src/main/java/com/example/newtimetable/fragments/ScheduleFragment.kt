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
import com.example.newtimetable.dialogs.DeleteDialog
import com.example.newtimetable.interfaces.DialogListener
import com.example.newtimetable.interfaces.ItemTouchHelperLestener
import com.example.newtimetable.interfaces.OnClickBtnListener
import com.example.newtimetable.modules.SwipeDragItemHelper
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : AbstractTabFragment(), OnClickBtnListener, ItemTouchHelperLestener, DialogListener {
    private lateinit var daySchedule: String
    private var itemId: Int? = null
    private var clock: String? = null
    private var hours: Int? = null
    private var minutes: Int? = null
    private var lesson: String? = null
    private var teacher: String? = null
    private var nameClass: String? = null
    private lateinit var database: SQLiteDatabase
    private lateinit var listItem: ArrayList<RecyclerSchedule>
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
        listItem = ArrayList()
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

    override fun onClickBtnListener(position: Int) {

    }

    override fun onItemDismiss(position: Int) {
        item = listItem[position]
        listItem.removeAt(position)
        itemAdapter.notifyItemRemoved(position)

        val dialogDelete: DialogFragment = DeleteDialog(this, position)
        fragmentManager?.let { dialogDelete.show(it, "deleteDialog") }
    }

    override fun onClickPositiveDialog() {
        database.delete(ScheduleDBHelper(context).TABLE_SCHEDULE, ScheduleDBHelper(context).KEY_ID + " = " + item.itemId, null)
    }

    override fun onClickNegativeFialog(position: Int) {
        listItem.add(position, item)
        itemAdapter.notifyItemInserted(position)
    }
}