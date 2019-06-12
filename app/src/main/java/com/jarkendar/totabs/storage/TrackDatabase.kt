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
    }

}