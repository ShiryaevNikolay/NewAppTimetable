package com.example.newtimetable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.tv_day_main_activity)
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                textView.text = "Понедельник"
            }
            Calendar.TUESDAY -> {
                textView.text = "Вторник"
            }
            Calendar.WEDNESDAY -> {
                textView.text = "Среда"
            }
            Calendar.THURSDAY -> {
                textView.text = "Четверг"
            }
            Calendar.FRIDAY -> {
                textView.text = "Пятница"
            }
            Calendar.SATURDAY -> {
                textView.text = "Суббота"
            }
            Calendar.SUNDAY -> {
                textView.text = "Воскресенье"
            }
        }

//        val navigationView: NavigationView = findViewById(R.id.nav_view_main_activity)
//        navigationView.setNavigationItemSelectedListener {
//
//        }
    }
}
