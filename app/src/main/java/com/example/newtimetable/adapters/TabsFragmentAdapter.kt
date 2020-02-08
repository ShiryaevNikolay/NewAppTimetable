package com.example.newtimetable.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.newtimetable.fragments.AbstractTabFragment
import com.example.newtimetable.fragments.ScheduleFragment
import com.example.newtimetable.util.RequestCode

class TabsFragmentAdapter(
    context: Context,
    fm: FragmentManager,
    database: SQLiteDatabase,
    requestCode: Int
) : FragmentPagerAdapter(fm) {

    private lateinit var tabs: MutableMap<Int, AbstractTabFragment>

    init {
        initTabsMap(context, database, requestCode)
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
    private fun initTabsMap(context: Context, database: SQLiteDatabase, requestCode: Int) {
        tabs = HashMap()
        tabs[0] = ScheduleFragment().getInstance(context,0, database, requestCode)
        tabs[1] = ScheduleFragment().getInstance(context,1, database, requestCode)
        tabs[2] = ScheduleFragment().getInstance(context,2, database, requestCode)
        tabs[3] = ScheduleFragment().getInstance(context,3, database, requestCode)
        tabs[4] = ScheduleFragment().getInstance(context,4, database, requestCode)
        tabs[5] = ScheduleFragment().getInstance(context,5, database, requestCode)
        if (requestCode == RequestCode().REQUEST_CODE_MAIN)
            tabs[6] = ScheduleFragment().getInstance(context,6, database, requestCode)
    }
}