package com.example.newtimetable.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LessonDBHelper(
    context: Context?,
    name: String? = "lessonDB",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    val TABLE_LESSON: String = "lesson"
    val KEY_ID: String = "_id"
    val KEY_TEXT: String = "lesson"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_LESSON($KEY_ID INTEGER PRIMARY KEY, $KEY_TEXT TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LESSON")

        onCreate(db)
    }

}