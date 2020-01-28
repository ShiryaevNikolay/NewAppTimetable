package com.example.newtimetable

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val navigationView: BottomNavigationView = findViewById(R.id.nav_view_main_activity)
        navigationView.setOnNavigationItemSelectedListener {
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
                    val intent = Intent(this, ListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_homework -> {
                    val intent = Intent(this, ListActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
