package com.example.newtimetable

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                tv_day_main_activity.text = "Понедельник"
            }
            Calendar.TUESDAY -> {
                tv_day_main_activity.text = "Вторник"
            }
            Calendar.WEDNESDAY -> {
                tv_day_main_activity.text = "Среда"
            }
            Calendar.THURSDAY -> {
                tv_day_main_activity.text = "Четверг"
            }
            Calendar.FRIDAY -> {
                tv_day_main_activity.text = "Пятница"
            }
            Calendar.SATURDAY -> {
                tv_day_main_activity.text = "Суббота"
            }
            Calendar.SUNDAY -> {
                tv_day_main_activity.text = "Воскресенье"
            }
        }

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
                    val intent = Intent(this, ScheduleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_homework -> {

                    true
                }
                else -> false
            }
        }
    }
}
