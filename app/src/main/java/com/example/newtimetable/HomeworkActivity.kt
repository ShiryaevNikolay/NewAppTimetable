package com.example.newtimetable

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.newtimetable.adapters.HomeworkAdapter
import com.example.newtimetable.database.HomeworkDBHelper
import com.example.newtimetable.dialogs.CustomDialog
import com.example.newtimetable.interfaces.DialogDeleteListener
import com.example.newtimetable.interfaces.OnClickItemListener
import com.example.newtimetable.interfaces.OnLongClickItemListener
import com.example.newtimetable.util.RequestCode
import kotlinx.android.synthetic.main.activity_homework.*
import kotlinx.android.synthetic.main.content_homework.*

class HomeworkActivity : AppCompatActivity(), OnClickItemListener, OnLongClickItemListener, DialogDeleteListener {
    private var itemId: Int? = null
    private var task: String? = null
    private var textAddDate: String? = null
    private var textToDate: String? = null
    private lateinit var database: SQLiteDatabase
    private var listItem: ArrayList<RecyclerHomework> = ArrayList()
    private var listItemRemove: ArrayList<RecyclerHomework> = ArrayList()
    private var positionItemRemove: ArrayList<Int> = ArrayList()
    private lateinit var homeworkAdapter: HomeworkAdapter

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework)

        toolbar.menu.getItem(0).isVisible = false
        toolbar.menu.getItem(1).isVisible = false
        toolbar.menu.getItem(1).setOnMenuItemClickListener {
            for (i in 0 until listItem.size) {
                listItem[i].checkBox = false
            }
            homeworkAdapter.notifyDataSetChanged()
            checkVisibleBtnRemove()
            return@setOnMenuItemClickListener true
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }

        fab_add.setOnClickListener {
            val intent = Intent(this, AddHomeworkActivity::class.java)
            intent.putExtra("requestCode", RequestCode().REQUEST_CODE_HOMEWORK)
            startActivityForResult(intent, RequestCode().REQUEST_CODE_HOMEWORK)
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
                homeworkAdapter.notifyItemRemoved(positionItemRemove[i])
            }
            val dialogDelete: DialogFragment = CustomDialog(this, listItemRemove.size)
            dialogDelete.show(this.supportFragmentManager, "dialogDelete")
        }
        checkVisibleBtnRemove()

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        database = HomeworkDBHelper(this).writableDatabase

        fillingList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues()
            contentValues.put(HomeworkDBHelper(this).KEY_TASK, data?.getStringExtra("task"))
            contentValues.put(HomeworkDBHelper(this).KEY_ADD_DATE, data?.getStringExtra("addDate"))
            contentValues.put(HomeworkDBHelper(this).KEY_TO_DATE, data?.getStringExtra("toDate"))
            if (requestCode == RequestCode().REQUEST_CODE_HOMEWORK) {
                database.insert(HomeworkDBHelper(this).TABLE_HOMEWORK, null, contentValues)
            } else if (requestCode == RequestCode().REQUEST_CODE_HOMEWORK_CHANGE) {
                database.update(HomeworkDBHelper(this).TABLE_HOMEWORK, contentValues, HomeworkDBHelper(this).KEY_ID + " = " + listItem[data?.extras?.getInt("position")!!].itemId, null)
            }
            fillingList()

        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun fillingList() {
        listItem.clear()
        val cursor: Cursor = database.query(HomeworkDBHelper(this).TABLE_HOMEWORK, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                itemId = cursor.getInt(cursor.getColumnIndex(HomeworkDBHelper(this).KEY_ID))
                task = cursor.getString(cursor.getColumnIndex(HomeworkDBHelper(this).KEY_TASK))
                textAddDate = cursor.getString(cursor.getColumnIndex(HomeworkDBHelper(this).KEY_ADD_DATE))
                textToDate = cursor.getString(cursor.getColumnIndex(HomeworkDBHelper(this).KEY_TO_DATE))
                listItem.add(RecyclerHomework(itemId!!, task!!, false , textAddDate!!, textToDate!!))
            } while (cursor.moveToNext())
        }
        cursor.close()
        homeworkAdapter = HomeworkAdapter(listItem, this, this)
        recyclerView.adapter = homeworkAdapter
    }

    override fun onClickItemListener(position: Int) {
        val intent = Intent(this, AddHomeworkActivity::class.java)
        intent.putExtra("requestCode", RequestCode().REQUEST_CODE_HOMEWORK_CHANGE)
        intent.putExtra("task", listItem[position].task)
        intent.putExtra("addDate", listItem[position].textAddData)
        intent.putExtra("toDate", listItem[position].textToData)
        intent.putExtra("position", position)
        startActivityForResult(intent, RequestCode().REQUEST_CODE_HOMEWORK_CHANGE)
    }

    override fun onLongClickItemListener(position: Int, remove: Boolean) {
        listItem[position].checkBox = remove
        homeworkAdapter.notifyDataSetChanged()
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

    override fun onClickPositiveDialog() {
        for (i in 0 until listItemRemove.size) {
            database.delete(HomeworkDBHelper(this).TABLE_HOMEWORK, HomeworkDBHelper(this).KEY_ID + " = " + listItemRemove[i].itemId, null)
        }
        listItemRemove.clear()
        homeworkAdapter.notifyDataSetChanged()
        checkVisibleBtnRemove()
    }

    override fun onClickNegativeDialog(position: Int) {
        for (i in 0 until positionItemRemove.size) {
            listItem.add(positionItemRemove[i], listItemRemove[i])
            homeworkAdapter.notifyItemInserted(positionItemRemove[i])
        }
        listItemRemove.clear()
        positionItemRemove.clear()
        checkVisibleBtnRemove()
    }
}
