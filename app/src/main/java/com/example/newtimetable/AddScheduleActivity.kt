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
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.dialogs.RadioDialog
import com.example.newtimetable.interfaces.DialogAddInputListener
import com.example.newtimetable.interfaces.DialogRadioButtonListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_add_schedule.toolbar
import java.util.*

class AddScheduleActivity : AppCompatActivity(), View.OnClickListener, MenuItem.OnMenuItemClickListener, DialogAddInputListener, DialogRadioButtonListener {
    private var day: String? = ""
    private var clockStart: String? = ""
    private var hoursStart: Int = 0
    private var minutesStart: Int = 0
    private var clockEnd: String? = ""
    private var hoursEnd: Int = 0
    private var minutesEnd: Int = 0
    private var week: String = "12"
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

        tv_clock_start_schedule.setOnClickListener(this)
        tv_clock_start_schedule.text = ""
        tv_clock_end_schedule.setOnClickListener(this)
        tv_clock_end_schedule.text = ""
        tv_clock_schedule_icon.setOnClickListener(this)
        tv_lesson_schedule.setOnClickListener(this)
        tv_lesson_schedule.text = ""
        tv_lesson_schedule_icon.setOnClickListener(this)
        tv_teacher_schedule.setOnClickListener(this)
        tv_teacher_schedule.text = ""
        tv_teacher_schedule_icon.setOnClickListener(this)
        tv_class_schedule.setOnClickListener(this)
        tv_class_schedule.text = ""
        tv_class_schedule_icon.setOnClickListener(this)
        tv_week_schedule.setOnClickListener(this)
        tv_week_schedule_icon.setOnClickListener(this)
        tr_week_schedule.isVisible =
            PreferenceManager.getDefaultSharedPreferences(this).getString("number_0f_week", "1") != "1"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode().REQUEST_CODE_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.getStringExtra("selectBtn") == "lesson") {
                    tv_lesson_schedule.text = data.getStringExtra("text")
                    if (data.getStringExtra("type") != "") {
                        tv_lesson_schedule.text = (tv_lesson_schedule.text as String?).plus("(" + data.getStringExtra("type") + ")")
                    }
                    checkEmptyField()
                } else if (data?.getStringExtra("selectBtn") == "teacher") {
                    tv_teacher_schedule.text = data.getStringExtra("text")
                    checkEmptyField()
                }
                checkEmptyField()
            }
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
            R.id.tv_lesson_schedule,
            R.id.tv_lesson_schedule_icon -> {
                val intent = Intent(this, SelectItemListActivity::class.java)
                intent.putExtra("selectBtn", "lesson")
                startActivityForResult(intent, RequestCode().REQUEST_CODE_LIST)
            }
            R.id.tv_teacher_schedule,
            R.id.tv_teacher_schedule_icon -> {
                val intent = Intent(this, SelectItemListActivity::class.java)
                intent.putExtra("selectBtn", "teacher")
                startActivityForResult(intent, RequestCode().REQUEST_CODE_LIST)
            }
            R.id.tv_class_schedule,
            R.id.tv_class_schedule_icon -> {
                val addClassDialog: DialogFragment = CustomDialog(this, "addClass")
                addClassDialog.show(supportFragmentManager, "addClassDialog")
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

    override fun onClickPositiveDialog(text: String) {
        tv_class_schedule.text = text
        checkEmptyField()
    }

    override fun onClickNegativeDialog() {
        checkEmptyField()
    }

    private fun checkEmptyField() {
        if (tv_clock_start_schedule.text != "" &&
            tv_lesson_schedule.text != ""
        ) {
            val background = fab_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
            fab_ok.background = background
        } else {
            val background = fab_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
            fab_ok.background = background
        }
    }

    private fun clickAddBtn() {
        if (tv_clock_start_schedule.text != "" && tv_lesson_schedule.text != "") {
            val data = Intent()
            data.putExtra("day", day)
            data.putExtra("clockStart", tv_clock_start_schedule.text)
            data.putExtra("hoursStart", hoursStart)
            data.putExtra("minutesStart", minutesStart)
            data.putExtra("clockEnd", tv_clock_end_schedule.text)
            data.putExtra("hoursEnd", hoursEnd)
            data.putExtra("minutesEnd", minutesEnd)
            data.putExtra("lesson", tv_lesson_schedule.text)
            data.putExtra("teacher", tv_teacher_schedule.text)
            data.putExtra("numberClass", tv_class_schedule.text)
            data.putExtra("week", week)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onClickRadioButtonDialog(choose: String) {
        if (checkCondition(choose)) {
            when (choose) {
                "1" -> {
                    week = "1"
                    tv_week_schedule.text = this.resources.getString(R.string.week1)
                }
                "2" -> {
                    week = "2"
                    tv_week_schedule.text = this.resources.getString(R.string.week2)
                }
                "12" -> {
                    week = "12"
                    tv_week_schedule.text = this.resources.getString(R.string.schedule_week)
                }
            }
        } else {
            Toast.makeText(this, "В это время уже есть занятие", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickNegativeButtonDialog(choose: String) {
        if (checkCondition("12")) {
            week = "12"
            tv_week_schedule.text = this.resources.getString(R.string.schedule_week)
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
