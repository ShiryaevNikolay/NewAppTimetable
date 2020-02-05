package com.example.newtimetable

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.preference.PreferenceManager
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_item.*
import java.util.*

class AddHomeworkActivity : AppCompatActivity(), View.OnClickListener {
    private var task: String = ""
    private var textAddDate: String = ""
    private var textToDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(1).isVisible = false
        toolbar.menu.getItem(0).setOnMenuItemClickListener {
            sendTask()
            return@setOnMenuItemClickListener true
        }
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        fab_ok.setOnClickListener { sendTask() }

        tv_date_homework.setOnClickListener(this)
        tv_date_homework_icon.setOnClickListener(this)

        textAddDate = java.text.SimpleDateFormat("dd.MMMyyyy", Locale.getDefault()).format(Date())

        if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_HOMEWORK_CHANGE) {
            textInputHomeworkTask.setText(intent.extras!!.getString("task"))
            task = intent.extras!!.getString("task").toString()
            textAddDate = intent.extras!!.getString("addDate").toString()
            textToDate = intent.extras!!.getString("toDate").toString()
            tv_date_homework.text = textToDate
            val background = fab_ok.background
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
            fab_ok.background = background
        }

        textInputHomeworkTask.addTextChangedListener {
            checkSendBtn(it.toString())
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_date_homework,
            R.id.tv_date_homework_icon -> {
                callDatePicker()
            }
        }
    }

    private fun callDatePicker() {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthCurrent = calendar.get(Calendar.MONTH)
        val yearCurrent = calendar.get(Calendar.YEAR)
        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            run {
                var fullDateFix: String = if (dayOfMonth < 10) "0$dayOfMonth." else "$dayOfMonth."
                when (month) {
                    0 -> fullDateFix += "янв"
                    1 -> fullDateFix += "февр"
                    2 -> fullDateFix += "мар"
                    3 -> fullDateFix += "апр"
                    4 -> fullDateFix += "мая"
                    5 -> fullDateFix += "июн"
                    6 -> fullDateFix += "июл"
                    7 -> fullDateFix += "авг"
                    8 -> fullDateFix += "сент"
                    9 -> fullDateFix += "окт"
                    10 -> fullDateFix += "нояб"
                    11 -> fullDateFix += "дек"
                }
                fullDateFix += ".$year"
                tv_date_homework.text = fullDateFix
                textToDate = fullDateFix
                checkSendBtn(textInputHomeworkTask.text.toString())
            }
        }, yearCurrent, monthCurrent, day)
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
    }

    private fun sendTask() {
        if (task != "") {
            val data = Intent()
            data.putExtra("task", task)
            data.putExtra("addDate", textAddDate)
            data.putExtra("toDate", textToDate)
            if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_HOMEWORK_CHANGE) {
                data.putExtra("position", intent.extras!!.getInt("position"))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private fun checkSendBtn(it: String) {
        val background = fab_ok.background
        if (it == "") {
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
        } else {
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
        }
        task = it
        fab_ok.background = background
    }
}
