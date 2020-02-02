package com.example.newtimetable.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper


class HomeworkDBHelper(
    context: Context?,
    name: String? = "homeworkDB",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {
    val TABLE_HOMEWORK = "homework"
    val KEY_ID = "_id"
    val KEY_TASK = "task"
    val KEY_ADD_DATE = "addDate"
    val KEY_TO_DATE = "toDate"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_HOMEWORK($KEY_ID INTEGER PRIMARY KEY, $KEY_TASK TEXT, $KEY_ADD_DATE TEXT, $KEY_TO_DATE TEXT)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HOMEWORK")
        onCreate(db)
    }
}