package com.example.newtimetable

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.ScheduleAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnClickItemListener {

    private var day = ""
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
    private var listItem: ArrayList<RecyclerSchedule> = ArrayList()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") == "1") {
            tv_week_main_activity.isVisible = false
        } else {
            tv_week_main_activity.isVisible = true
            if (PreferenceManager.getDefaultSharedPreferences(this).getString("this_week", "1") == "1") {
                tv_week_main_activity.text = this.resources.getString(R.string.week1)
            } else if (PreferenceManager.getDefaultSharedPreferences(this).getString("this_week", "2") == "2") {
                tv_week_main_activity.text = this.resources.getString(R.string.week2)
            }
        }

        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                tv_day_main_activity.text = "Понедельник"
                day = "mon"
            }
            Calendar.TUESDAY -> {
                tv_day_main_activity.text = "Вторник"
                day = "tues"
            }
            Calendar.WEDNESDAY -> {
                tv_day_main_activity.text = "Среда"
                day = "wed"
            }
            Calendar.THURSDAY -> {
                tv_day_main_activity.text = "Четверг"
                day = "thurs"
            }
            Calendar.FRIDAY -> {
                tv_day_main_activity.text = "Пятница"
                day = "fri"
            }
            Calendar.SATURDAY -> {
                tv_day_main_activity.text = "Суббота"
                day = "sat"
            }
            Calendar.SUNDAY -> {
                tv_day_main_activity.text = "Воскресенье"
                day = "sun"
            }
        }

        rv_main_activity.layoutManager = LinearLayoutManager(this)
        rv_main_activity.setHasFixedSize(true)

        fillingList()

        nav_view_main_activity.setOnNavigationItemSelectedListener {
            when(it.itemId) {R.id.nav_schedule -> {
                    startActivityForResult(Intent(this, ScheduleActivity::class.java), RequestCode().REQUEST_CODE_MAIN)
                    true
                }
                R.id.nav_homework -> {
                    startActivity(Intent(this, HomeworkActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode().REQUEST_CODE_MAIN) {
            fillingList()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("Recycle")
    private fun fillingList() {
        var flag = false
        database = ScheduleDBHelper(this).writableDatabase
        listItem.clear()
        val cursor: Cursor = database.query(ScheduleDBHelper(this).TABLE_SCHEDULE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_DAY)) == day) {
                    flag = true
                    itemId = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_ID))
                    clockStart = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK_START))
                    hoursStart = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_HOURS_START))
                    minutesStart = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_MINUTES_START))
                    clockEnd = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK_END))
                    hoursEnd = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_HOURS_END))
                    minutesEnd = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_MINUTES_END))
                    lesson = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_LESSON))
                    teacher = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_TEACHER))
                    nameClass = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLASS))
                    week = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK))
                    if (PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") == "1") {
                        sortList(hoursStart!!, minutesStart!!)
                    } else {
                        sortListWeek()
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        itemAdapter = ScheduleAdapter(listItem, RequestCode().REQUEST_CODE_MAIN, this)
        tv_day_off_main_activity.isVisible = !flag
        rv_main_activity.adapter = itemAdapter
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

    private fun sortListWeek() {
        if (listItem.isNotEmpty()) {
            var flagLoopOne = false
            for (i in 0 until listItem.size) {
                if (PreferenceManager.getDefaultSharedPreferences(this).getString("this_week", "1") != week && week != "12")
                    continue
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
            if (!flagLoopOne && (PreferenceManager.getDefaultSharedPreferences(this).getString("this_week", "1") == week || week == "12")) {
                listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
            }
        } else if (PreferenceManager.getDefaultSharedPreferences(this).getString("this_week", "1") == week || week == "12") {
            listItem.add(RecyclerSchedule(itemId!!, clockStart!!, clockEnd!!, hoursStart!!, minutesStart!!, hoursEnd!!, minutesEnd!!, lesson!!, teacher!!, nameClass!!, week!!))
        }
    }

    override fun onClickItemListener(position: Int) {
        //...
    }
}
