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

        if (intent?.getStringExtra("requestCode").equals("btn_lesson")) {
            textInputLayoutLesson.visibility = View.VISIBLE
            textInputLayoutSurname.visibility = View.GONE
            textInputLayoutName.visibility = View.GONE
            textInputLayoutPatronymic.visibility = View.GONE
        } else if (intent?.getStringExtra("requestCode").equals("btn_teacher")) {
            textInputLayoutLesson.visibility = View.GONE
            textInputLayoutSurname.visibility = View.VISIBLE
            textInputLayoutName.visibility = View.VISIBLE
            textInputLayoutPatronymic.visibility = View.VISIBLE
        }

        btn_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_ok.setOnClickListener(this)

        if (intent?.getStringExtra("requestCode").equals("btn_lesson")) {
            textInputLesson.addTextChangedListener(this)
        } else if (intent?.getStringExtra("requestCode").equals("btn_teacher")) {
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
        btn_ok.setBackgroundDrawable(background)
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
        btn_ok.setBackgroundDrawable(background)
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
        btn_ok.setBackgroundDrawable(background)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.done_btn_toolbar) {
            if (intent?.getStringExtra("requestCode").equals("btn_lesson")) {
                text = textInputLesson.text.toString()
            } else if (intent?.getStringExtra("requestCode").equals("btn_teacher")) {
                text = textInputSurname.text.toString() + " " + textInputName.text.toString() + " " + textInputPatronymic.text.toString()
            }
            val data = Intent()
            data.putExtra("text", text)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
        return true
    }

    override fun onClick(v: View?) {
        if (textInputLesson.text.toString() != "") {
            if (intent?.getStringExtra("requestCode").equals("btn_lesson")) {
                text = textInputLesson.text.toString()
            } else if (intent?.getStringExtra("requestCode").equals("btn_teacher")) {
                text = textInputSurname.text.toString() + " " + textInputName.text.toString() + " " + textInputPatronymic.text.toString()
            }
            val data = Intent()
            data.putExtra("text", text)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}
