package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.ScheduleAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var day = ""
    private var itemId: Int? = null
    private var clock: String? = null
    private var hours: Int? = null
    private var minutes: Int? = null
    private var lesson: String? = null
    private var teacher: String? = null
    private var nameClass: String? = null
    private var listItem: ArrayList<RecyclerSchedule> = ArrayList()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: ScheduleAdapter
    private lateinit var stylePref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        stylePref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        var theme: Int = stylePref.getInt("THEME", R.style.AppTheme)
        setTheme(theme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            when(it.itemId) {
                R.id.nav_lesson -> {
                    val intent = Intent(this, ListActivity::class.java)
                    intent.putExtra("onBtn", "btn_lesson")
                    startActivity(intent)
                    true
                }
                R.id.nav_teacher -> {
                    val intent = Intent(this, ListActivity::class.java)
                    intent.putExtra("onBtn", "btn_teacher")
                    startActivity(intent)
                    true
                }
                R.id.nav_schedule -> {
                    startActivityForResult(Intent(this, ScheduleActivity::class.java), RequestCode().REQUEST_CODE_MAIN)
                    true
                }
                R.id.nav_homework -> {
                    startActivity(Intent(this, HomeworkActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    stylePref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = stylePref.edit()
                    if (stylePref.getInt("THEME", R.style.AppTheme) == R.style.AppTheme)
                        editor.putInt("THEME", R.style.AppTheme_Dark)
                    else
                        editor.putInt("THEME", R.style.AppTheme)
                    editor.apply()
                    theme = stylePref.getInt("THEME", R.style.AppTheme)
                    setTheme(theme)
                    recreate()
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode().REQUEST_CODE_MAIN) {
            if (resultCode == Activity.RESULT_OK) {
                fillingList()
            }
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
                    clock = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK))
                    hours = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_HOURS))
                    minutes = cursor.getInt(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_MINUTES))
                    lesson = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_LESSON))
                    teacher = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_TEACHER))
                    nameClass = cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLASS))
                    sortList(hours!!, minutes!!)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        itemAdapter = ScheduleAdapter(listItem)
        tv_day_off_main_activity.isVisible = !flag
        rv_main_activity.adapter = itemAdapter
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
