package com.example.newtimetable

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newtimetable.adapters.TabsFragmentAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.fragments.ScheduleFragment
import com.example.newtimetable.interfaces.OnClickBtnListener
import kotlinx.android.synthetic.main.activity_schedule.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var database: SQLiteDatabase
    var btnListener: OnClickBtnListener = ScheduleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        toolbar.setNavigationOnClickListener {
//            setResult(Activity.RESULT_OK)
            finish()
        }

        val scheduleDBHelper = ScheduleDBHelper(this)
        database = scheduleDBHelper.writableDatabase
        initTabs()

        fab.setOnClickListener { btnListener.onClickBtnListener(tabLayout.selectedTabPosition) }
    }

    private fun initTabs() {
        val adapter = TabsFragmentAdapter(this, supportFragmentManager, database)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}
