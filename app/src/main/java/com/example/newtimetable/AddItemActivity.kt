package com.example.newtimetable

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_add_item.*

class AddItemActivity : AppCompatActivity(), TextWatcher, MenuItem.OnMenuItemClickListener, View.OnClickListener {

    var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(1).isVisible = false
        toolbar.menu.getItem(0).setOnMenuItemClickListener(this)
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            textInputLayoutLesson.visibility = View.VISIBLE
            if (intent.extras?.getInt("requestCode") == RequestCode().REQUEST_CODE_LIST_CHANGE) {
                text = intent?.getStringExtra("text").toString()
                textInputLesson.setText(text)
            }
            textInputLayoutSurname.visibility = View.GONE
            textInputLayoutName.visibility = View.GONE
            textInputLayoutPatronymic.visibility = View.GONE
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            textInputLayoutLesson.visibility = View.GONE
            textInputLayoutSurname.visibility = View.VISIBLE
            textInputLayoutName.visibility = View.VISIBLE
            textInputLayoutPatronymic.visibility = View.VISIBLE
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
            textInputLesson.addTextChangedListener(this)
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            textInputSurname.addTextChangedListener(this)
            textInputName.addTextChangedListener(this)
            textInputPatronymic.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val background = btn_ok.background
        if (s.toString() == "") {
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
        } else {
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
        }
        btn_ok.background = background
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val background = btn_ok.background
        if (s.toString() == "") {
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
        } else {
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
        }
        btn_ok.background = background
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val background = btn_ok.background
        if (s.toString() == "") {
            background.setTint(ContextCompat.getColor(this, R.color.colorNotActive))
            toolbar.menu.getItem(0).isVisible = false
        } else {
            background.setTint(ContextCompat.getColor(this, R.color.colorAccent))
            toolbar.menu.getItem(0).isVisible = true
        }
        btn_ok.background = background
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
