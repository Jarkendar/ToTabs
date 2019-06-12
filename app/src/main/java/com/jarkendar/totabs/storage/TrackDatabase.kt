package com.jarkendar.totabs.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

public class TrackDatabase constructor(val context: Context) : SQLiteOpenHelper(context, "TrackDatabase", null, 1) {


    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        upgradeDatabase(sqLiteDatabase, 0, 1)
        Log.d(TAG, "create database $sqLiteDatabase")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        upgradeDatabase(sqLiteDatabase, oldVersion, newVersion)
        Log.d(TAG, "upgrade database $sqLiteDatabase, from $oldVersion to $newVersion")
    }

    private fun upgradeDatabase(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //todo create database


    }


    companion object {
        private val TAG = "********"

        private val FIELD_ROW_ID: String = "_id"

        private val TABLE_TRACK: String = "TABLE_TRACK"

        private val FIELD_TRACK_NAME: String = "TRACK_NAME"
        private val FIELD_BEATS_PER_MINUTE: String = "BEATS_PER_MINUTE"
        private val FIELD_MIN_NOTE: String = "MINIMAL_NOTE"
        private val FIELD_MIN_NOTE_DURATION: String = "MINIMAL_NOTE_DURATION"
        private val FIELD_LIST_OF_NOTES: String = "LIST_OF_NOTES"

        private val TABLE_NOTES: String = "TABLE_NOTES"

        private val FIELD_NOTE_NAME: String = "NOTE_NAME"
        private val FIELD_FREQUENCY: String = "FREQUENCY"
        private val FIELD_STAFF_POSITION: String = "STAFF_POSITION"
        private val FIELD_IS_HALF_TONE: String = "IS_HALF_TONE"
        private val FIELD_AMPLITUDE: String = "AMPLITUDE"
        private val FIELD_LENGTH: String = "LENGTH"
    }

}