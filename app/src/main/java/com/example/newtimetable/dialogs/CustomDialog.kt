package com.example.newtimetable.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.newtimetable.R
import com.example.newtimetable.interfaces.DialogAddInputListener
import com.example.newtimetable.interfaces.DialogDeleteListener
import kotlinx.android.synthetic.main.dialog_add_input.view.*
import kotlinx.android.synthetic.main.dialog_delete.view.dialog_btn_negative
import kotlinx.android.synthetic.main.dialog_delete.view.dialog_btn_positive

class CustomDialog() : DialogFragment(), View.OnClickListener, TextWatcher {

    private var title: String = "delete"
    private var textInput: String = ""
    private var position: Int? = null
    private lateinit var dialogDeleteListener: DialogDeleteListener
    private lateinit var dialogAddInputListener: DialogAddInputListener

    constructor(dialogDeleteListener: DialogDeleteListener, position: Int) : this() {
        this.dialogDeleteListener = dialogDeleteListener
        this.position = position
    }

    constructor(dialogAddInputListener: DialogAddInputListener, title: String) : this() {
        this.dialogAddInputListener = dialogAddInputListener
        this.title = title
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.dialog_delete, null)
        when (title) {
            "addClass" -> {
                view = inflater.inflate(R.layout.dialog_add_input, null)
                view.textInputDialog.addTextChangedListener(this)
                view.textInputLayoutDialog.hint = context?.resources?.getString(R.string.dialog_class_input)
            }
            "addLesson" -> {
                view = inflater.inflate(R.layout.dialog_add_input, null)
                view.textInputDialog.addTextChangedListener(this)
                view.textInputLayoutDialog.hint = context?.resources?.getString(R.string.dialog_lesson_input)
                view.title_dialog.isVisible = false
            }
            "addTeacher" -> {
                view = inflater.inflate(R.layout.dialog_add_input, null)
                view.textInputDialog.addTextChangedListener(this)
                view.textInputLayoutDialog.hint = context?.resources?.getString(R.string.dialog_teacher_input)
                view.title_dialog.isVisible = false
            }
        }
        view.dialog_btn_negative.setOnClickListener(this)
        view.dialog_btn_positive.setOnClickListener(this)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.dialog_btn_negative) {
            if (title == "delete") {
                position?.let { dialogDeleteListener.onClickNegativeDialog(it) }
            } else if (title == "addClass") {
                dialogAddInputListener.onClickNegativeDialog()
            }
        } else if (v?.id == R.id.dialog_btn_positive) {
            if (title == "delete") {
                dialogDeleteListener.onClickPositiveDialog()
            } else if (title == "addClass" || title == "addLesson" || title == "addTeacher") {
                dialogAddInputListener.onClickPositiveDialog(textInput)
            }
        }
        dismiss()
    }

    override fun afterTextChanged(s: Editable?) { textInput = s.toString() }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { textInput = s.toString() }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { textInput = s.toString() }
}