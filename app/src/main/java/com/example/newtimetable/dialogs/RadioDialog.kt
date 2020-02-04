package com.example.newtimetable.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newtimetable.R
import com.example.newtimetable.interfaces.DialogRadioButtonListener
import kotlinx.android.synthetic.main.dialog_choose_week.view.*

class RadioDialog(private var dialogRadioButtonListener: DialogRadioButtonListener, private var week: String) : DialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_choose_week, container)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
        if (week == "1")
            view.radioButtonWeek1.isChecked = true
        else if (week == "2")
            view.radioButtonWeek2.isChecked = true
        view.radioButtonWeek1.setOnClickListener(this)
        view.radioButtonWeek2.setOnClickListener(this)
        view.dialog_btn_negative.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.radioButtonWeek1 -> {
                dialogRadioButtonListener.onClickRadioButtonDialog("1")
            }
            R.id.radioButtonWeek2 -> {
                dialogRadioButtonListener.onClickRadioButtonDialog("2")
            }
            R.id.dialog_btn_negative -> {
                dialogRadioButtonListener.onClickNegativeButtonDialog("12")
            }
        }
        dismiss()
    }
}