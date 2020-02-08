package com.example.newtimetable.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newtimetable.R
import kotlinx.android.synthetic.main.fragment_this_day.view.*

class ThisDayFragment : AbstractTabFragment() {

    private lateinit var thisDay: String

    fun getInstance(context: Context, position: Int) : ThisDayFragment {
        val args = Bundle()
        val fragment = ThisDayFragment()
        fragment.arguments = args
        when (position) {
            0 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_mon)
            1 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_tues)
            2 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_wed)
            3 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_thurs)
            4 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_fri)
            5 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_sat)
            6 -> fragment.thisDay = context.resources.getString(R.string.main_activity_this_day_sun)
        }
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_this_day, container, false)
        view.tv_day_main_activity.text = thisDay
        return view
    }
}