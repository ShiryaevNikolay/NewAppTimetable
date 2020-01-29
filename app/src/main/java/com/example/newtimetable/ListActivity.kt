package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.LessonTeacherAdapter
import com.example.newtimetable.database.LessonDBHelper
import com.example.newtimetable.database.TeacherDBHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity : AppCompatActivity() {
    private val REQUEST_CODE_LIST = 0
    private var listItem = ArrayList<RecyclerItem>()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper
    private lateinit var teacherDBHelper: TeacherDBHelper
    private lateinit var cursor: Cursor

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            toolbar.title = "Занятия"
            lessonDBHelper = LessonDBHelper(this)
            database = lessonDBHelper.writableDatabase
            cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            toolbar.title = "Преподаватели"
            teacherDBHelper = TeacherDBHelper(this)
            database = teacherDBHelper.writableDatabase
            cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, null, null, null, null, null)
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fab.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("requestCode", getIntent().getStringExtra("onBtn"))
            startActivityForResult(intent, REQUEST_CODE_LIST)
        }

        if (cursor.moveToFirst()) {
            do {
                if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
                    fillingOutTeacherList(cursor, teacherDBHelper)
                }
            } while (cursor.moveToNext())
        }

        rv_list_activity.layoutManager = LinearLayoutManager(this)
        rv_list_activity.setHasFixedSize(true)
        itemAdapter = LessonTeacherAdapter(listItem)
        rv_list_activity.adapter = itemAdapter
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues()
            if (data?.getStringExtra("requestCode") == "btn_lesson") {
                contentValues.put(lessonDBHelper.KEY_TEXT, data.getStringExtra("text"))
                database.insert(lessonDBHelper.TABLE_LESSON, null, contentValues)
                cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
            } else if (data?.getStringExtra("requestCode") == "btn_teacher") {
                contentValues.put(teacherDBHelper.KEY_SURNAME, data.getStringExtra("surname"))
                contentValues.put(teacherDBHelper.KEY_NAME, data.getStringExtra("name"))
                contentValues.put(teacherDBHelper.KEY_PATRONYMIC, data.getStringExtra("patronymic"))
                database.insert(teacherDBHelper.TABLE_TEACHER, null, contentValues)
                cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, null, null, null, null, null)
            }
            listItem.clear()
            if (cursor.moveToFirst()) {
                do {
                    if (data?.getStringExtra("requestCode") == "btn_lesson") {
                        fillingOutLessonList(cursor, lessonDBHelper)
                    } else if (data?.getStringExtra("requestCode") == "btn_teacher") {
                        fillingOutTeacherList(cursor, teacherDBHelper)
                    }
                } while (cursor.moveToNext())
            }
            itemAdapter = LessonTeacherAdapter(listItem)
            rv_list_activity.adapter = itemAdapter
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fillingOutLessonList(cursor: Cursor, itemDBHelper: LessonDBHelper) {
        val text: String = cursor.getString(cursor.getColumnIndex(itemDBHelper.KEY_TEXT))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(itemDBHelper.KEY_ID))
        listItem.add(RecyclerItem(text, itemId))
    }

    private fun fillingOutTeacherList(cursor: Cursor, teacherDBHelper: TeacherDBHelper) {
        val surname: String = cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_SURNAME))
        val name: String = cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_NAME))
        val patronymic: String = cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_PATRONYMIC))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(teacherDBHelper.KEY_ID))
        listItem.add(RecyclerItem("$surname $name $patronymic", itemId))
    }
}
