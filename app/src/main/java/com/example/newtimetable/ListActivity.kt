package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newtimetable.adapters.LessonTeacherAdapter
import com.example.newtimetable.database.LessonDBHelper
import com.example.newtimetable.database.TeacherDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.DialogDeleteListener
import com.example.newtimetable.interfaces.ItemTouchHelperLestener
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.modules.SwipeDragItemHelper
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity :
    AppCompatActivity(), OnClickItemListener, ItemTouchHelperLestener, DialogDeleteListener {

    private var listItem = ArrayList<RecyclerItem>()
    private lateinit var database: SQLiteDatabase
    private lateinit var itemAdapter: LessonTeacherAdapter
    private lateinit var lessonDBHelper: LessonDBHelper
    private lateinit var teacherDBHelper: TeacherDBHelper
    private lateinit var cursor: Cursor
    private lateinit var itemList: RecyclerItem

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getSharedPreferences("MyPref", Context.MODE_PRIVATE).getInt("THEME", R.style.AppTheme))

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
            intent.putExtra("onBtn", getIntent().getStringExtra("onBtn"))
            startActivityForResult(intent, RequestCode().REQUEST_CODE_LIST)
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

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues()
            if (data?.getStringExtra("onBtn") == "btn_lesson") {
                contentValues.put(lessonDBHelper.KEY_TEXT, data.getStringExtra("text"))
                if (requestCode == RequestCode().REQUEST_CODE_LIST_CHANGE) {
                    database.update(lessonDBHelper.TABLE_LESSON, contentValues, lessonDBHelper.KEY_ID + " = " + listItem[data.extras?.getInt("position")!!].itemId, null)
                } else if (requestCode == RequestCode().REQUEST_CODE_LIST) {
                    database.insert(lessonDBHelper.TABLE_LESSON, null, contentValues)
                }
                cursor = database.query(lessonDBHelper.TABLE_LESSON, null, null, null, null, null, null)
            } else if (data?.getStringExtra("onBtn") == "btn_teacher") {
                contentValues.put(teacherDBHelper.KEY_SURNAME, data.getStringExtra("surname"))
                contentValues.put(teacherDBHelper.KEY_NAME, data.getStringExtra("name"))
                contentValues.put(teacherDBHelper.KEY_PATRONYMIC, data.getStringExtra("patronymic"))
                if (requestCode == RequestCode().REQUEST_CODE_LIST_CHANGE) {
                    database.update(teacherDBHelper.TABLE_TEACHER, contentValues, teacherDBHelper.KEY_ID + " = " + listItem[data.extras?.getInt("position")!!].itemId, null)
                } else if (requestCode == RequestCode().REQUEST_CODE_LIST) {
                    database.insert(teacherDBHelper.TABLE_TEACHER, null, contentValues)
                }
                cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, null, null, null, null, null)
            }
            listItem.clear()
            if (cursor.moveToFirst()) {
                do {
                    if (data?.getStringExtra("onBtn") == "btn_lesson") {
                        fillingOutLessonList(cursor, lessonDBHelper)
                    } else if (data?.getStringExtra("onBtn") == "btn_teacher") {
                        fillingOutTeacherList(cursor, teacherDBHelper)
                    }
                } while (cursor.moveToNext())
            }
            itemAdapter = LessonTeacherAdapter(listItem, this)
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

    override fun onClickItemListener(position: Int) {
        val intent = Intent(this, AddItemActivity::class.java)
        intent.putExtra("onBtn", getIntent().getStringExtra("onBtn"))
        intent.putExtra("requestCode", RequestCode().REQUEST_CODE_LIST_CHANGE)
        intent.putExtra("position", position)
        if (this.intent.getStringExtra("onBtn") == "btn_lesson") {
            intent.putExtra("text", listItem[position].text)
        } else if (this.intent.getStringExtra("onBtn") == "btn_teacher") {
            cursor = database.query(teacherDBHelper.TABLE_TEACHER, null, teacherDBHelper.KEY_ID + " = ?", arrayOf(listItem[position].itemId.toString()), null, null, null)
            cursor.moveToFirst()
            intent.putExtra("surname", cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_SURNAME)))
            intent.putExtra("name", cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_NAME)))
            intent.putExtra("patronymic", cursor.getString(cursor.getColumnIndex(teacherDBHelper.KEY_PATRONYMIC)))
        }
        intent.putExtra("itemId", listItem[position].itemId)
        startActivityForResult(intent, RequestCode().REQUEST_CODE_LIST_CHANGE)
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
}
