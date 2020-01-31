package com.example.newtimetable.fragments

import android.content.Context
import androidx.fragment.app.Fragment

abstract class AbstractTabFragment : Fragment() {

    private lateinit var title: String

    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }
}