package com.example.newtimetable.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TeacherDBHelper(
    context: Context?,
    name: String? = "teacherDB",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    val TABLE_TEACHER: String = "teacher"
    val KEY_ID: String = "_id"
    val KEY_FULLNAME: String = "fullName"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_TEACHER($KEY_ID INTEGER PRIMARY KEY, $KEY_FULLNAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHER")

        onCreate(db)
    }
}