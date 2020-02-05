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
    val KEY_CLOCK_START: String = "clockStart"
    val KEY_CLOCK_END: String = "clockEnd"
    val KEY_HOURS_START: String = "hoursStart"
    val KEY_MINUTES_START: String = "minutesStart"
    val KEY_HOURS_END: String = "hoursEND"
    val KEY_MINUTES_END: String = "minutesEND"
    val KEY_LESSON: String = "lesson"
    val KEY_TEACHER: String = "teacher"
    val KEY_CLASS: String = "class"
    val KEY_WEEK: String = "week"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_SCHEDULE($KEY_ID INTEGER PRIMARY KEY, " +
                                                        "$KEY_DAY TEXT, $KEY_CLOCK_START TEXT, $KEY_CLOCK_END TEXT, " +
                                                        "$KEY_HOURS_START INTEGER, $KEY_MINUTES_START INTEGER, " +
                                                        "$KEY_HOURS_END INTEGER, $KEY_MINUTES_END INTEGER, " +
                                                        "$KEY_LESSON TEXT, $KEY_TEACHER TEXT, " +
                                                        "$KEY_CLASS TEXT, $KEY_WEEK TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULE")

        onCreate(db)
    }
}