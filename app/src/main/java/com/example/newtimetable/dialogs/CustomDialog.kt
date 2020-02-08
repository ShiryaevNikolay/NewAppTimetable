package com.example.newtimetable.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newtimetable.R
import com.example.newtimetable.interfaces.DialogDeleteListener
import kotlinx.android.synthetic.main.dialog_delete.view.dialog_btn_negative
import kotlinx.android.synthetic.main.dialog_delete.view.dialog_btn_positive

class CustomDialog() : DialogFragment(), View.OnClickListener, TextWatcher {

    private var textInput: String = ""
    private var position: Int? = null
    private lateinit var dialogDeleteListener: DialogDeleteListener

    constructor(dialogDeleteListener: DialogDeleteListener, position: Int) : this() {
        this.dialogDeleteListener = dialogDeleteListener
        this.position = position
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_delete, null)
        view.dialog_btn_negative.setOnClickListener(this)
        view.dialog_btn_positive.setOnClickListener(this)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.dialog_btn_negative) {
            position?.let { dialogDeleteListener.onClickNegativeDialog(it) }
        } else if (v?.id == R.id.dialog_btn_positive) {
            dialogDeleteListener.onClickPositiveDialog()
        }
        dismiss()
    }

    override fun afterTextChanged(s: Editable?) { textInput = s.toString() }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { textInput = s.toString() }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { textInput = s.toString() }
}