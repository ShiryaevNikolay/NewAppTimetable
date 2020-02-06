package com.example.newtimetable

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.LessonTeacherAdapter
import com.example.newtimetable.database.LessonDBHelper
import com.example.newtimetable.database.TeacherDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.*
import com.example.newtimetable.modules.SwipeDragItemHelper
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity :
    AppCompatActivity(), OnClickItemListener, ItemTouchHelperLestener, DialogDeleteListener, DialogAddInputListener, OnLongClickItemListener {

    private var listItem = ArrayList<RecyclerItem>()
    private var listItemRemove: ArrayList<RecyclerItem> = ArrayList()
    private var positionItemRemove: ArrayList<Int> = ArrayList()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper
    private lateinit var teacherDBHelper: TeacherDBHelper
    private lateinit var cursor: Cursor

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
        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(1).isVisible = false
        toolbar.menu.getItem(1).setOnMenuItemClickListener {
            for (i in 0 until listItem.size) {
                listItem[i].checkBox = false
            }
            itemAdapter.notifyDataSetChanged()
            checkVisibleBtnRemove()
            return@setOnMenuItemClickListener true
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fab_add.setOnClickListener {
            if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
                val dialogAdd: DialogFragment = CustomDialog(this, "addLesson")
                dialogAdd.show(this.supportFragmentManager, "dialogAddLesson")
            } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
                val dialogAdd: DialogFragment = CustomDialog(this, "addTeacher")
                dialogAdd.show(this.supportFragmentManager, "dialogAddTeacher")
            }
        }
        fab_remove.setOnClickListener {
            positionItemRemove.clear()
            for (i in 0 until listItem.size) {
                if (listItem[i].checkBox) {
                    listItemRemove.add(listItem[i])
                    positionItemRemove.add(i)
                }
            }
            for (i in positionItemRemove.size-1 downTo 0) {
                listItem.removeAt(positionItemRemove[i])
                itemAdapter.notifyItemRemoved(positionItemRemove[i])
            }
            val dialogDelete: DialogFragment = CustomDialog(this, listItemRemove.size)
            dialogDelete.show(this.supportFragmentManager, "dialogDelete")
        }
        checkVisibleBtnRemove()

        var flag = true
        if (cursor.moveToFirst()) {
            do {
                flag = false
                if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
                    fillingOutLessonList(cursor, lessonDBHelper)
                } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
                    fillingOutTeacherList(cursor, teacherDBHelper)
                }
            } while (cursor.moveToNext())
        }
        tv_empty_list_activity.isVisible = flag

        rv_list_activity.layoutManager = LinearLayoutManager(this)
        rv_list_activity.setHasFixedSize(true)
        itemAdapter = LessonTeacherAdapter(listItem, this, this)
        rv_list_activity.adapter = itemAdapter

        val callback: ItemTouchHelper.Callback = SwipeDragItemHelper(this, this)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rv_list_activity)
    }

    override fun onClickItemListener(position: Int) {
        if (intent?.getStringExtra("onBtn").equals("btn_lesson")) {
            val dialogAdd: DialogFragment = CustomDialog(this, "addLesson", true, listItem[position].text, listItem[position].itemId)
            dialogAdd.show(this.supportFragmentManager, "dialogAddLesson")
        } else if (intent?.getStringExtra("onBtn").equals("btn_teacher")) {
            val dialogAdd: DialogFragment = CustomDialog(this, "addTeacher", true, listItem[position].text, listItem[position].itemId)
            dialogAdd.show(this.supportFragmentManager, "dialogAddTeacher")
        }
    }

    override fun onItemDismiss(position: Int) {
        positionItemRemove.clear()
        listItemRemove.add(listItem[position])
        positionItemRemove.add(position)
        listItem.removeAt(position)
        itemAdapter.notifyItemRemoved(position)

        val dialogDelete: DialogFragment = CustomDialog(this, position)
        dialogDelete.show(this.supportFragmentManager, "dialogDelete")
    }

    override fun onClickPositiveDialog() {
        for (i in 0 until listItemRemove.size) {
            if (intent.getStringExtra("onBtn") == "btn_lesson") {
                database.delete(lessonDBHelper.TABLE_LESSON, lessonDBHelper.KEY_ID + " = " + listItemRemove[i].itemId, null)
            } else if (intent.getStringExtra("onBtn") == "btn_teacher") {
                database.delete(teacherDBHelper.TABLE_TEACHER, teacherDBHelper.KEY_ID + " = " + listItemRemove[i].itemId, null)
            }
        }
        listItemRemove.clear()
        itemAdapter.notifyDataSetChanged()
        tv_empty_list_activity.isVisible = itemAdapter.itemCount == 0
        checkVisibleBtnRemove()
    }

    override fun onClickNegativeDialog(position: Int) {
        for (i in 0 until positionItemRemove.size) {
            listItem.add(positionItemRemove[i], listItemRemove[i])
            itemAdapter.notifyItemInserted(positionItemRemove[i])
        }
        listItemRemove.clear()
        positionItemRemove.clear()
        tv_empty_list_activity.isVisible = itemAdapter.itemCount == 0
        checkVisibleBtnRemove()
    }

    override fun onClickPositiveDialog(text: String, requestCode: Boolean, itemId: Int) {
        val contentValues = ContentValues()
        if (intent.getStringExtra("onBtn") == "btn_lesson") {
            contentValues.put(lessonDBHelper.KEY_LESSON, text)
            if (requestCode)
                database.update(lessonDBHelper.TABLE_LESSON, contentValues, lessonDBHelper.KEY_ID + " = " + itemId, null)
            else
                database.insert(lessonDBHelper.TABLE_LESSON, null, contentValues)
            cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
        } else if (intent.getStringExtra("onBtn") == "btn_teacher") {
            contentValues.put(teacherDBHelper.KEY_FULLNAME, text)
            if (requestCode)
                database.update(teacherDBHelper.TABLE_TEACHER, contentValues, teacherDBHelper.KEY_ID + " = " + itemId, null)
            else
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
        itemAdapter = LessonTeacherAdapter(listItem, this, this)
        rv_list_activity.adapter = itemAdapter
        tv_empty_list_activity.isVisible = itemAdapter.itemCount == 0
        checkVisibleBtnRemove()
    }

    override fun onClickNegativeDialog() {
        //...
    }

    private fun fillingOutLessonList(cursor: Cursor, itemDBHelper: LessonDBHelper) {
        val text: String = cursor.getString(cursor.getColumnIndex(itemDBHelper.KEY_LESSON))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(itemDBHelper.KEY_ID))
        listItem.add(RecyclerItem(text, itemId, false))
    }

    private fun fillingOutTeacherList(cursor: Cursor, teacherDBHelper: TeacherDBHelper) {
        val teacher: String = cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_FULLNAME))
        val itemId: Int = cursor.getInt(cursor.getColumnIndex(teacherDBHelper.KEY_ID))
        listItem.add(RecyclerItem(teacher, itemId, false))
    }

    override fun onLongClickItemListener(position: Int, remove: Boolean) {
        listItem[position].checkBox = remove
        itemAdapter.notifyDataSetChanged()
        checkVisibleBtnRemove()
    }

    private fun checkVisibleBtnRemove() {
        var flagCheckIfRemove = false
        for (i in 0 until listItem.size) {
            if (listItem[i].checkBox) {
                flagCheckIfRemove = true
                break
            }
        }
        toolbar.menu.getItem(1).isVisible = flagCheckIfRemove
        fab_remove.isVisible = flagCheckIfRemove
    }
}
