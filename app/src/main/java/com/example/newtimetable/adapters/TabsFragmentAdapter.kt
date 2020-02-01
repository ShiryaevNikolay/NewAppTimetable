package com.example.newtimetable.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newtimetable.fragments.AbstractTabFragment
import com.example.newtimetable.fragments.ScheduleFragment

class TabsFragmentAdapter(
    context: Context,
    fm: FragmentManager,
    database: SQLiteDatabase
) : FragmentPagerAdapter(fm) {

    private lateinit var tabs: MutableMap<Int, AbstractTabFragment>

    init {
        initTabsMap(context, database)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs.getValue(position).getTitle()
    }

    override fun getItem(position: Int): Fragment {
        return tabs.getValue(position)
    }

    override fun getCount(): Int {
        return tabs.size
    }

    @SuppressLint("UseSparseArrays")
    private fun initTabsMap(context: Context, database: SQLiteDatabase) {
        tabs = HashMap()
        tabs[0] = ScheduleFragment().getInstance(context,0, database)
        tabs[1] = ScheduleFragment().getInstance(context,1, database)
        tabs[2] = ScheduleFragment().getInstance(context,2, database)
        tabs[3] = ScheduleFragment().getInstance(context,3, database)
        tabs[4] = ScheduleFragment().getInstance(context,4, database)
        tabs[5] = ScheduleFragment().getInstance(context,5, database)
    }
}