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
        if (oldVersion < 1) {
            val queryCreateTableNotes = "CREATE TABLE $TABLE_NOTES (" +
                    "$FIELD_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FIELD_LIST_OF_NOTES TEXT NOT NULL, " +
                    "$FIELD_NOTE_NAME TEXT NOT NULL, " +
                    "$FIELD_FREQUENCY DOUBLE NOT NULL, " +
                    "$FIELD_STAFF_POSITION FLOAT NOT NULL, " +
                    "$FIELD_IS_HALF_TONE INTEGER DEFAULT $FALSE, " +
                    "$FIELD_AMPLITUDE DOUBLE DEFAULT 0.0, " +
                    "$FIELD_LENGTH DOUBLE DEFAULT $DEFAULT_NOTE_LENGTH " +
                    ");"
            Log.d(TAG, "create table $queryCreateTableNotes")
            sqLiteDatabase!!.execSQL(queryCreateTableNotes)

            val queryCreateTableTrack = "CREATE TABLE $TABLE_TRACK (" +
                    "$FIELD_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FIELD_TRACK_NAME TEXT NOT NULL, " +
                    "$FIELD_BEATS_PER_MINUTE INTEGER NOT NULL, " +
                    "$FIELD_MIN_NOTE DOUBLE DEFAULT $DEFAULT_NOTE_LENGTH, " +
                    "$FIELD_MIN_NOTE_DURATION DOUBLE DEFAULT 1.0, " +
                    "$FIELD_LIST_OF_NOTES TEXT NOT NULL, " +
                    "FOREIGN KEY ($FIELD_LIST_OF_NOTES) REFERENCES $TABLE_NOTES($FIELD_LIST_OF_NOTES)" +
                    ");"
            Log.d(TAG, "create table $queryCreateTableTrack")
            sqLiteDatabase!!.execSQL(queryCreateTableTrack)
        }
    }


    companion object {
        private val TAG = "********"

        private val TRUE = 1
        private val FALSE = 0
        private val DEFAULT_NOTE_LENGTH = 1.0

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