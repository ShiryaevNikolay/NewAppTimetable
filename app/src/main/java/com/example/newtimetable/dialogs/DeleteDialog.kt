package com.example.newtimetable.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newtimetable.R
import com.example.newtimetable.interfaces.DialogListener
import kotlinx.android.synthetic.main.dialog_delete.view.*

class DeleteDialog(val dialogListener: DialogListener, val position: Int) : DialogFragment(), View.OnClickListener {
    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_style)
        val view: View = inflater.inflate(R.layout.dialog_delete, null)
        view.dialog_btn_negative.setOnClickListener(this)
        view.dialog_btn_positive.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.dialog_btn_negative) {
            dialogListener.onClickNegativeFialog(position)
        } else if (v?.id == R.id.dialog_btn_positive) {
            dialogListener.onClickPositiveDialog()
        }
        dismiss()
    }
}