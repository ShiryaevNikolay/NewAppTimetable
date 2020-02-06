package com.example.newtimetable.interfaces

interface DialogAddInputListener {
    fun onClickPositiveDialog(text: String, requestCode: Boolean, itemId: Int)
    fun onClickNegativeDialog()
}