package com.example.newtimetable

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.newtimetable.adapters.TabsFragmentAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_schedule.*

class ScheduleActivity : AppCompatActivity() {
    private lateinit var adapter: TabsFragmentAdapter
    private lateinit var database: SQLiteDatabase
    private var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        val scheduleDBHelper = ScheduleDBHelper(this)
        database = scheduleDBHelper.writableDatabase
        initTabs()

        fab.setOnClickListener {
            val intent = Intent(this, AddScheduleActivity::class.java)
            tabPosition = tabLayout.selectedTabPosition
            when (tabPosition) {
                0 -> intent.putExtra("day", "mon")
                1 -> intent.putExtra("day", "tues")
                2 -> intent.putExtra("day", "wed")
                3 -> intent.putExtra("day", "thurs")
                4 -> intent.putExtra("day", "fri")
                5 -> intent.putExtra("day", "sat")
            }
            startActivityForResult(intent, RequestCode().REQUEST_CODE_SCHEDULE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues()
            contentValues.put(ScheduleDBHelper(this).KEY_DAY, data?.getStringExtra("day"))
            contentValues.put(ScheduleDBHelper(this).KEY_CLOCK_START, data?.getStringExtra("clockStart"))
            contentValues.put(ScheduleDBHelper(this).KEY_HOURS_START, data?.extras?.getInt("hoursStart"))
            contentValues.put(ScheduleDBHelper(this).KEY_MINUTES_START, data?.extras?.getInt("minutesStart"))
            contentValues.put(ScheduleDBHelper(this).KEY_CLOCK_END, data?.getStringExtra("clockEnd"))
            contentValues.put(ScheduleDBHelper(this).KEY_HOURS_END, data?.extras?.getInt("hoursEnd"))
            contentValues.put(ScheduleDBHelper(this).KEY_MINUTES_END, data?.extras?.getInt("minutesEnd"))
            contentValues.put(ScheduleDBHelper(this).KEY_LESSON, data?.getStringExtra("lesson"))
            contentValues.put(ScheduleDBHelper(this).KEY_TEACHER, data?.getStringExtra("teacher"))
            contentValues.put(ScheduleDBHelper(this).KEY_CLASS, data?.getStringExtra("numberClass"))
            contentValues.put(ScheduleDBHelper(this).KEY_WEEK, data?.getStringExtra("week"))
            if (requestCode == RequestCode().REQUEST_CODE_SCHEDULE) {
                database.insert(ScheduleDBHelper(this).TABLE_SCHEDULE, null, contentValues)
            } else {
                database.update(ScheduleDBHelper(this).TABLE_SCHEDULE, contentValues, ScheduleDBHelper(this).KEY_ID + " = " + data?.extras?.getInt("itemId"), null)
                when (data?.getStringExtra("day")) {
                    "mon" -> tabPosition = 0
                    "tues" -> tabPosition = 1
                    "wed" -> tabPosition = 2
                    "thurs" -> tabPosition = 3
                    "fri" -> tabPosition = 4
                    "sat" -> tabPosition = 5
                }
            }
            initTabs()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initTabs() {
        adapter = TabsFragmentAdapter(this, supportFragmentManager, database)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setScrollPosition(tabPosition, 0f, true)
        viewPager.currentItem = tabPosition
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
