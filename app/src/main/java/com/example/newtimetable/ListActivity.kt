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
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity : AppCompatActivity() {
    private val REQUEST_CODE_LIST = 0
    private var listItem = ArrayList<RecyclerItem>()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            toolbar.title = "Занятия"
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            toolbar.title = "Преподаватели"
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fab.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("requestCode", getIntent().getStringExtra("onBtn"))
            startActivityForResult(intent, REQUEST_CODE_LIST)
        }

        lessonDBHelper = LessonDBHelper(this)
        database = lessonDBHelper.writableDatabase
        val cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {

                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        rv_list_activity.layoutManager = LinearLayoutManager(this)
        rv_list_activity.setHasFixedSize(true)
        itemAdapter = LessonTeacherAdapter(listItem)
        rv_list_activity.adapter = itemAdapter
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues()
            contentValues.put(lessonDBHelper.KEY_TEXT, data?.getStringExtra("text"))
            database.insert(lessonDBHelper.TABLE_LESSON, null, contentValues)
            listItem.clear()
            val cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } while (cursor.moveToNext())
            }
            cursor.close()
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

//    private fun fillingOutTeacherList(cursor: Cursor) {
//        val text: String = cursor.getString(cursor.getColumnIndex())
//        val itemId: Int = cursor.getInt(cursor.getColumnIndex())
//        listItem.add(RecyclerItem(text, itemId))
//    }
}
