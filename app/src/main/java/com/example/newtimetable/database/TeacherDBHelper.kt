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

    val TABLE_TEACHER: String = "lesson"
    val KEY_ID: String = "_id"
    val KEY_SURNAME: String = "surname"
    val KEY_NAME: String = "name"
    val KEY_PATRONYMIC: String = "patronymic"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_TEACHER($KEY_ID INTEGER PRIMARY KEY, $KEY_SURNAME TEXT, $KEY_NAME TEXT, $KEY_PATRONYMIC TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEACHER")

        onCreate(db)
    }
}