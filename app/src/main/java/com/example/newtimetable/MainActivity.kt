package com.example.newtimetable

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.preference.PreferenceManager
import com.example.newtimetable.adapters.DaysFragmentAdapter
import com.example.newtimetable.adapters.TabsFragmentAdapter
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.viewPager
import java.util.*

class MainActivity : AppCompatActivity(), OnClickItemListener {

    private lateinit var database: SQLiteDatabase
    private var tabPosition: Int = 0

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
            Calendar.MONDAY -> tabPosition = 0
            Calendar.TUESDAY -> tabPosition = 1
            Calendar.WEDNESDAY -> tabPosition = 2
            Calendar.THURSDAY -> tabPosition = 3
            Calendar.FRIDAY -> tabPosition = 4
            Calendar.SATURDAY -> tabPosition = 5
            Calendar.SUNDAY -> tabPosition = 6
        }

        database = ScheduleDBHelper(this).writableDatabase
        initTabs()

        nav_view_main_activity.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_schedule -> {
                    tabPosition = viewPager.currentItem
                    startActivityForResult(Intent(this, ScheduleActivity::class.java), RequestCode().REQUEST_CODE_MAIN)
                    true
                }
                R.id.nav_homework -> {
                    startActivity(Intent(this, HomeworkActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode().REQUEST_CODE_MAIN) {
            initTabs()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClickItemListener(position: Int) {
        //...
    }

    private fun initTabs() {
        val adapter = TabsFragmentAdapter(this, supportFragmentManager, database, RequestCode().REQUEST_CODE_MAIN)
        val adapterThisDay = DaysFragmentAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        viewPagerDay.adapter = adapterThisDay
        viewPager.currentItem = tabPosition
        viewPagerDay.currentItem = tabPosition
    }
}
