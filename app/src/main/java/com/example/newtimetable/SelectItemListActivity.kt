package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.LessonTeacherAdapter
import com.example.newtimetable.database.LessonDBHelper
import com.example.newtimetable.database.TeacherDBHelper
import com.example.newtimetable.interfaces.OnClickItemListener
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.toolbar
import kotlinx.android.synthetic.main.content_list.*

class SelectItemListActivity : AppCompatActivity(), OnClickItemListener {
    private var listItem: ArrayList<RecyclerItem> = ArrayList()
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper
    private lateinit var teacherDBHelper: TeacherDBHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var cursor: Cursor

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        fab.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        toolbar.subtitle = "Выберите"

        if (intent.getStringExtra("selectBtn") == "lesson") {
            toolbar.title = "Занятия"
            lessonDBHelper = LessonDBHelper(this)
            database = lessonDBHelper.writableDatabase
            cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
        } else if (intent.getStringExtra("selectBtn") == "teacher") {
            toolbar.title = "Преподаватели"
            teacherDBHelper = TeacherDBHelper(this)
            database = teacherDBHelper.writableDatabase
            cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, null, null, null, null, null)
        }

        if (cursor.moveToFirst()) {
            do {
                if (intent.getStringExtra("selectBtn") == "lesson") {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } else if (intent.getStringExtra("selectBtn") == "teacher") {
                    fillingOutTeacherList(cursor, teacherDBHelper)
                }
            } while (cursor.moveToNext())
        }

        rv_list_activity.layoutManager = LinearLayoutManager(this)
        rv_list_activity.setHasFixedSize(true)
        itemAdapter = LessonTeacherAdapter(listItem, this)
        rv_list_activity.adapter = itemAdapter
    }

    private fun fillingOutLessonList(cursor: Cursor, itemDBHelper: LessonDBHelper) {
        val text: String = cursor.getString(cursor.getColumnIndex(itemDBHelper.KEY_LESSON))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(itemDBHelper.KEY_ID))
        listItem.add(RecyclerItem(text, itemId))
    }

    private fun fillingOutTeacherList(cursor: Cursor, teacherDBHelper: TeacherDBHelper) {
        val teacher: String = cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_FULLNAME))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(teacherDBHelper.KEY_ID))
        listItem.add(RecyclerItem(teacher, itemId))
    }

    override fun onClickItemListener(position: Int) {
        val data = Intent()
        data.putExtra("text", listItem[position].text)
        if (intent.getStringExtra("selectBtn") == "lesson") {
            data.putExtra("selectBtn", "lesson")
        } else if (intent.getStringExtra("selectBtn") == "teacher") {
            data.putExtra("selectBtn", "teacher")
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}