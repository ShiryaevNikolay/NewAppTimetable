package com.example.newtimetable.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newtimetable.fragments.AbstractTabFragment
import com.example.newtimetable.fragments.ThisDayFragment

class DaysFragmentAdapter(
    var context: Context,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    private lateinit var dayTab: MutableMap<Int, AbstractTabFragment>

    init {
        initDayTabMap()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return dayTab.getValue(position).getTitle()
    }

    override fun getItem(position: Int): Fragment {
        return dayTab.getValue(position)
    }

    override fun getCount(): Int {
        return dayTab.size
    }

    private fun initDayTabMap() {
        dayTab = HashMap()
        dayTab[0] = ThisDayFragment().getInstance(context, 0)
        dayTab[1] = ThisDayFragment().getInstance(context, 1)
        dayTab[2] = ThisDayFragment().getInstance(context, 2)
        dayTab[3] = ThisDayFragment().getInstance(context, 3)
        dayTab[4] = ThisDayFragment().getInstance(context, 4)
        dayTab[5] = ThisDayFragment().getInstance(context, 5)
        dayTab[6] = ThisDayFragment().getInstance(context, 6)
    }
}