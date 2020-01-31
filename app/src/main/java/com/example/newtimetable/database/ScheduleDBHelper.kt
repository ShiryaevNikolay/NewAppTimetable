package com.example.newtimetable.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ScheduleDBHelper(
    context: Context?,
    name: String? = "scheduleDB",
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    val TABLE_SCHEDULE: String = "schedule"
    val KEY_ID: String = "_id"
    val KEY_DAY: String = "day"
    val KEY_CLOCK: String = "clock"
    val KEY_HOURS: String = "hours"
    val KEY_MINUTES: String = "minutes"
    val KEY_LESSON: String = "lesson"
    val KEY_TEACHER: String = "teacher"
    val KEY_CLASS: String = "class"
    val KEY_WEEK: String = "week"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_SCHEDULE($KEY_ID INTEGER PRIMARY KEY, $KEY_DAY TEXT, $KEY_CLOCK TEXT, $KEY_HOURS INTEGER, $KEY_MINUTES INTEGER, $KEY_LESSON TEXT, $KEY_TEACHER TEXT, $KEY_CLASS TEXT, $KEY_WEEK INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULE")

        onCreate(db)
    }
}