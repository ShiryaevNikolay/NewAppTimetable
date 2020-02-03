package com.example.newtimetable

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_item.*

class AddItemActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener, View.OnClickListener {

    var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getSharedPreferences("MyPref", Context.MODE_PRIVATE).getInt("THEME", R.style.AppTheme))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(1).isVisible = false
        toolbar.menu.getItem(0).setOnMenuItemClickListener(this)
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        textInputLayoutHomeworkTask.isVisible = false
        homework_choose_date.isVisible = false
        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            textInputLayoutLesson.isVisible = true
            if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_LIST_CHANGE) {
                text = intent?.getStringExtra("text").toString()
                textInputLesson.setText(text)
            }
            textInputLayoutSurname.isVisible = false
            textInputLayoutName.isVisible = false
            textInputLayoutPatronymic.isVisible = false
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            textInputLayoutLesson.isVisible = false
            textInputLayoutSurname.isVisible = true
            textInputLayoutName.isVisible = true
            textInputLayoutPatronymic.isVisible = true
            if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_LIST_CHANGE) {
                textInputSurname.setText(intent.getStringExtra("surname"))
                textInputName.setText(intent.getStringExtra("name"))
                textInputPatronymic.setText(intent.getStringExtra("patronymic"))
            }
        }

        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_ok.setOnClickListener(this)

        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            textInputLesson.addTextChangedListener {
                val background = btn_ok.background
                if (it.toString() == "") {
                    background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
                    toolbar.menu.getItem(0).isVisible = false
                } else {
                    background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                    toolbar.menu.getItem(0).isVisible = true
                }
                btn_ok.background = background
            }
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            textInputSurname.addTextChangedListener {
                val background = btn_ok.background
                if (it.toString() == "") {
                    background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
                    toolbar.menu.getItem(0).isVisible = false
                } else {
                    background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                    toolbar.menu.getItem(0).isVisible = true
                }
                btn_ok.background = background
            }
            textInputName.addTextChangedListener {
                val background = btn_ok.background
                if (it.toString() == "") {
                    background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
                    toolbar.menu.getItem(0).isVisible = false
                } else {
                    background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                    toolbar.menu.getItem(0).isVisible = true
                }
                btn_ok.background = background
            }
            textInputPatronymic.addTextChangedListener {
                val background = btn_ok.background
                if (it.toString() == "") {
                    background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
                    toolbar.menu.getItem(0).isVisible = false
                } else {
                    background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
                    toolbar.menu.getItem(0).isVisible = true
                }
                btn_ok.background = background
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.done_btn_toolbar) {
            clickBtnOk()
        }
        return true
    }

    override fun onClick(v: View?) {
        clickBtnOk()
    }

    private fun clickBtnOk() {
        val data = Intent()
        data.putExtra("onBtn", intent.getStringExtra("onBtn"))
        if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_LIST_CHANGE) {
            data.putExtra("position", intent.extras!!.getInt("position"))
        }
        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            if (textInputLesson.text.toString() != "") {
                text = textInputLesson.text.toString()
                data.putExtra("text", text)
            }
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            if (textInputSurname.text.toString() != "" || textInputName.text.toString() != "" || textInputPatronymic.text.toString() != "") {
                data.putExtra("surname", textInputSurname.text.toString())
                data.putExtra("name", textInputName.text.toString())
                data.putExtra("patronymic", textInputPatronymic.text.toString())
            }
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
