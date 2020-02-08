package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.dialogs.RadioDialog
import com.example.newtimetable.interfaces.DialogRadioButtonListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_add_schedule.toolbar
import java.util.*

class AddScheduleActivity : AppCompatActivity(), View.OnClickListener, MenuItem.OnMenuItemClickListener, DialogRadioButtonListener {
    private var day: String? = ""
    private var lesson: String? = ""
    private var teacher: String? = ""
    private var nameClass: String? = ""
    private var clockStart: String? = ""
    private var hoursStart: Int = 0
    private var minutesStart: Int = 0
    private var clockEnd: String? = ""
    private var hoursEnd: Int = 0
    private var minutesEnd: Int = 0
    private var week: String = ""
    private var scheduleDBHelper: ScheduleDBHelper = ScheduleDBHelper(this)
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(0).setOnMenuItemClickListener(this)
        toolbar.menu.getItem(1).isVisible = false

        fab_ok.setOnClickListener(this)

        day = intent.getStringExtra("day")

        database = scheduleDBHelper.writableDatabase

        if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_SCHEDULE_CHANGE) {
            day = intent.getStringExtra("day")
            tiet_lesson.setText(intent.getStringExtra("lesson"))
            lesson = intent.getStringExtra("lesson")
            tiet_teacher.setText(intent.getStringExtra("teacher"))
            teacher = intent.getStringExtra("teacher")
            tiet_class.setText(intent.getStringExtra("nameClass"))
            nameClass = intent.getStringExtra("nameClass")
            tv_clock_start_schedule.text = intent.getStringExtra("clockStart")
            clockStart = intent.getStringExtra("clockStart")
            tv_clock_end_schedule.text = intent.getStringExtra("clockEnd")
            clockEnd = intent.getStringExtra("clockEnd")
            iv_indicator_week_schedule.isVisible = true
            when (intent.getStringExtra("week")!!) {
                "1" -> {
                    week = "1"
                    tv_week_schedule.text = this.resources.getString(R.string.week1)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_circle)
                    iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))
                }
                "2" -> {
                    week = "2"
                    tv_week_schedule.text = this.resources.getString(R.string.week2)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_circle)
                    iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.colorRed))
                }
                "12" -> {
                    week = "12"
                    tv_week_schedule.text = this.resources.getString(R.string.dialog_every_week)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_checkbox_unselect)
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
                        iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.gray_700))
                    else
                        iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.gray_500))
                }
            }
            checkEmptyField()
            hoursStart = intent.extras!!.getInt("hoursStart")
            minutesStart = intent.extras!!.getInt("minutesStart")
            hoursEnd = intent.extras!!.getInt("hoursEnd")
            minutesEnd = intent.extras!!.getInt("minutesEnd")
        } else {
            tv_clock_start_schedule.text = ""
            tv_clock_end_schedule.text = ""
            iv_indicator_week_schedule.isVisible = false
        }
        tv_clock_start_schedule.setOnClickListener(this)
        tv_clock_end_schedule.setOnClickListener(this)
        tv_week_schedule.setOnClickListener(this)
        tr_week_schedule.isVisible =
            PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") != "1"
        tiet_lesson.addTextChangedListener {
            lesson = it.toString()
        }
        tiet_teacher.addTextChangedListener {
            teacher = it.toString()
        }
        tiet_class.addTextChangedListener {
            nameClass = it.toString()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_clock_start_schedule -> {
                callTimePicker(true)
                checkEmptyField()
            }
            R.id.tv_clock_end_schedule -> {
                callTimePicker(false)
                checkEmptyField()
            }
            R.id.tv_week_schedule,
            R.id.tv_week_schedule_icon -> {
                val chooseWeekDialog: DialogFragment = RadioDialog(this, week)
                chooseWeekDialog.show(supportFragmentManager, "chooseWeek")
            }
            R.id.fab_ok -> {
                clickAddBtn()
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("Recycle")
    fun callTimePicker(clock: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            run {
                var flag = true
                var fullTime: String = if (hourOfDay < 10) "0$hourOfDay:" else "$hourOfDay:"
                fullTime += if (minute < 10) "0$minute" else "$minute"
                val cursor: Cursor = database.query(ScheduleDBHelper(this).TABLE_SCHEDULE, null, null, null, null, null, null)
                if (cursor.moveToFirst()) {
                    do {
                        if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK_START)) == fullTime &&
                            cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_DAY)) == day
                        ) {
                            if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) == "12" && (week == "1" || week == "2")) {
                                flag = false
                                break
                            } else if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) != "12" &&
                                        week == "12") {
                                flag = false
                            } else if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) == week) {
                                flag = false
                                break
                            }
                        }
                    } while (cursor.moveToNext())
                }
                if (flag) {
                    if (clock) {
                        tv_clock_start_schedule.text = fullTime
                        clockStart = tv_clock_start_schedule.text.toString()
                        hoursStart = hourOfDay
                        minutesStart = minute
                    } else {
                        tv_clock_end_schedule.text = fullTime
                        clockEnd = tv_clock_start_schedule.text.toString()
                        hoursEnd = hourOfDay
                        minutesEnd = minute
                    }
                    checkEmptyField()
                } else {
                    Toast.makeText(this, "В это время уже есть занятие", Toast.LENGTH_SHORT).show()
                }
            }
        }, hour, minute, true)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        clickAddBtn()
        return true
    }

    private fun checkEmptyField() {
        if (tv_clock_start_schedule.text != "" &&
            lesson != ""
        ) {
            if ((PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") != "1" && week != "") ||
                PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") == "1") {
                val background = fab_ok.background
                background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                toolbar.menu.getItem(0).isVisible = true
                fab_ok.background = background
            }
        } else {
            val background = fab_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
            fab_ok.background = background
        }
    }

    private fun clickAddBtn() {
        if (tv_clock_start_schedule.text != "" && lesson != "") {
            if (PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") == "1") {
                week = "12"
            }
            val data = Intent()
            data.putExtra("day", day)
            data.putExtra("clockStart", tv_clock_start_schedule.text)
            data.putExtra("hoursStart", hoursStart)
            data.putExtra("minutesStart", minutesStart)
            data.putExtra("clockEnd", tv_clock_end_schedule.text)
            data.putExtra("hoursEnd", hoursEnd)
            data.putExtra("minutesEnd", minutesEnd)
            data.putExtra("lesson", lesson)
            data.putExtra("teacher", teacher)
            data.putExtra("numberClass", nameClass)
            data.putExtra("week", week)
            data.putExtra("itemId", intent.extras?.getInt("itemId"))
            data.putExtra("position", intent.extras?.getInt("position"))
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onClickRadioButtonDialog(choose: String) {
        if (checkCondition(choose)) {
            iv_indicator_week_schedule.isVisible = true
            when (choose) {
                "1" -> {
                    week = "1"
                    tv_week_schedule.text = this.resources.getString(R.string.week1)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_circle)
                    iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))
                }
                "2" -> {
                    week = "2"
                    tv_week_schedule.text = this.resources.getString(R.string.week2)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_circle)
                    iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.colorRed))
                }
                "12" -> {
                    week = "12"
                    tv_week_schedule.text = this.resources.getString(R.string.dialog_every_week)
                    iv_indicator_week_schedule.setImageResource(R.drawable.ic_checkbox_unselect)
                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
                        iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.gray_700))
                    else
                        iv_indicator_week_schedule.setColorFilter(ContextCompat.getColor(this, R.color.gray_500))
                }
            }
            checkEmptyField()
        } else {
            Toast.makeText(this, "В это время уже есть занятие", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickNegativeButtonDialog(choose: String) {
        if (checkCondition("12")) {
            week = "12"
            tv_week_schedule.text = this.resources.getString(R.string.dialog_every_week)
        } else {
            Toast.makeText(this, "В это время уже есть занятие", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Recycle")
    private fun checkCondition(choose: String): Boolean {
        var flag = true
        val cursor: Cursor = database.query(ScheduleDBHelper(this).TABLE_SCHEDULE, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK_START)) == tv_clock_start_schedule.text &&
                    cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_DAY)) == day
                ) {
                    if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) == "12" && (choose == "1" || choose == "2")) {
                        flag = false
                        break
                    } else if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) != "12" &&
                        choose == "12") {
                        flag = false
                    } else if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_WEEK)) == choose) {
                        flag = false
                        break
                    }
                }
            } while (cursor.moveToNext())
        }
        return flag
    }
}
