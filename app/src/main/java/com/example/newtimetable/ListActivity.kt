package com.example.newtimetable

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.LessonTeacherAdapter
import com.example.newtimetable.database.LessonDBHelper
import com.example.newtimetable.database.TeacherDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.DialogAddInputListener
import com.example.newtimetable.interfaces.DialogDeleteListener
import com.example.newtimetable.interfaces.ItemTouchHelperLestener
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.modules.SwipeDragItemHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity :
    AppCompatActivity(), OnClickItemListener, ItemTouchHelperLestener, DialogDeleteListener, DialogAddInputListener {

    private var listItem = ArrayList<RecyclerItem>()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper
    private lateinit var teacherDBHelper: TeacherDBHelper
    private lateinit var cursor: Cursor
    private lateinit var itemList: RecyclerItem

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

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
            if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
                val dialogAdd: DialogFragment = CustomDialog(this, "addLesson")
                dialogAdd.show(this.supportFragmentManager, "dialogAddLesson")
            } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
                val dialogAdd: DialogFragment = CustomDialog(this, "addTeacher")
                dialogAdd.show(this.supportFragmentManager, "dialogAddTeacher")
            }
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
        itemAdapter = LessonTeacherAdapter(listItem, this)
        rv_list_activity.adapter = itemAdapter

        val callback: ItemTouchHelper.Callback = SwipeDragItemHelper(this, this)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rv_list_activity)
    }

    override fun onClickItemListener(position: Int) {
        if (this.intent.getStringExtra("onBtn") == "btn_lesson") {

        } else if (this.intent.getStringExtra("onBtn") == "btn_teacher") {

        }
    }

    override fun onItemDismiss(position: Int) {
        itemList = listItem[position]
        listItem.removeAt(position)
        itemAdapter.notifyItemRemoved(position)

        val dialogDelete: DialogFragment = CustomDialog(this, position)
        dialogDelete.show(this.supportFragmentManager, "dialogDelete")
    }

    override fun onClickPositiveDialog() {
        if (intent.getStringExtra("onBtn") == "btn_lesson") {
            database.delete(lessonDBHelper.TABLE_LESSON, lessonDBHelper.KEY_ID + " = " + itemList.itemId, null)
        } else if (intent.getStringExtra("onBtn") == "btn_teacher") {
            database.delete(teacherDBHelper.TABLE_TEACHER, teacherDBHelper.KEY_ID + " = " + itemList.itemId, null)
        }
    }

    override fun onClickNegativeDialog(position: Int) {
        listItem.add(position, itemList)
        itemAdapter.notifyItemInserted(position)
    }

    override fun onClickPositiveDialog(text: String) {
        val contentValues = ContentValues()
        if (intent.getStringExtra("onBtn") == "btn_lesson") {
            contentValues.put(lessonDBHelper.KEY_LESSON, text)
            database.insert(lessonDBHelper.TABLE_LESSON, null, contentValues)
            cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
        } else if (intent.getStringExtra("onBtn") == "btn_teacher") {
            contentValues.put(teacherDBHelper.KEY_FULLNAME, text)
            database.insert(teacherDBHelper.TABLE_TEACHER, null, contentValues)
            cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, null, null, null, null, null)
        }
        listItem.clear()
        if (cursor.moveToFirst()) {
            do {
                if (intent.getStringExtra("onBtn") == "btn_lesson") {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } else if (intent.getStringExtra("onBtn") == "btn_teacher") {
                    fillingOutTeacherList(cursor, teacherDBHelper)
                }
            } while (cursor.moveToNext())
        }
        itemAdapter = LessonTeacherAdapter(listItem, this)
        rv_list_activity.adapter = itemAdapter
    }

    override fun onClickNegativeDialog() {
        //...
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
}
