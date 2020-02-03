package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.newtimetable.database.ScheduleDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.DialogAddInputListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_add_schedule.toolbar
import java.util.*

class AddScheduleActivity : AppCompatActivity(), View.OnClickListener, MenuItem.OnMenuItemClickListener, DialogAddInputListener {
    private var day: String? = ""
    private var clock: String? = ""
    private var hours: Int = 0
    private var minutes: Int = 0
    private var scheduleDBHelper: ScheduleDBHelper = ScheduleDBHelper(this)
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getSharedPreferences("MyPref", Context.MODE_PRIVATE).getInt("THEME", R.style.AppTheme))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(0).setOnMenuItemClickListener(this)
        toolbar.menu.getItem(1).isVisible = false

        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_ok.setOnClickListener(this)

        day = intent.getStringExtra("day")

        database = scheduleDBHelper.writableDatabase

        tv_clock_schedule.setOnClickListener(this)
        tv_clock_schedule.text = ""
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode().REQUEST_CODE_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.getStringExtra("selectBtn") == "lesson") {
                    tv_lesson_schedule.text = data.getStringExtra("text")
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
            R.id.tv_clock_schedule,
            R.id.tv_clock_schedule_icon -> {
                callTimePicker()
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
            R.id.btn_ok -> {
                clickAddBtn()
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("Recycle")
    fun callTimePicker() {
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
                        if (cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_CLOCK)) == fullTime &&
                            cursor.getString(cursor.getColumnIndex(ScheduleDBHelper(this).KEY_DAY)) == day
                        ) {
                            flag = false
                        }
                    } while (cursor.moveToNext())
                }
                if (flag) {
                    tv_clock_schedule.text = fullTime
                    clock = tv_clock_schedule.text.toString()
                    hours = hourOfDay
                    minutes = minute
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
        if (tv_clock_schedule.text != "" &&
            tv_lesson_schedule.text != "" &&
            tv_teacher_schedule.text != "" &&
            tv_class_schedule.text != ""
        ) {
            val background = btn_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
            btn_ok.background = background
        } else {
            val background = btn_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
            btn_ok.background = background
        }
    }

    private fun clickAddBtn() {
        if (tv_clock_schedule.text != "" && tv_lesson_schedule.text != "" && tv_teacher_schedule.text != "" && tv_clock_schedule.text != "") {
            val data = Intent()
            data.putExtra("day", day)
            data.putExtra("clock", tv_clock_schedule.text)
            data.putExtra("hours", hours)
            data.putExtra("minutes", minutes)
            data.putExtra("lesson", tv_lesson_schedule.text)
            data.putExtra("teacher", tv_teacher_schedule.text)
            data.putExtra("numberClass", tv_class_schedule.text)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}
