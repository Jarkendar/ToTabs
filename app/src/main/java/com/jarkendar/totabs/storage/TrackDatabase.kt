package com.jarkendar.totabs.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.analyzer.note_parser.Note
import com.jarkendar.totabs.analyzer.note_parser.NoteLength
import com.jarkendar.totabs.analyzer.note_parser.Quartet
import java.text.SimpleDateFormat
import java.util.*

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
            val queryCreateTableTrack = "CREATE TABLE $TABLE_TRACK (" +
                    "$FIELD_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FIELD_TRACK_NAME TEXT NOT NULL, " +
                    "$FIELD_BEATS_PER_MINUTE INTEGER NOT NULL, " +
                    "$FIELD_MIN_NOTE DOUBLE DEFAULT $DEFAULT_NOTE_LENGTH, " +
                    "$FIELD_MIN_NOTE_DURATION DOUBLE DEFAULT 1.0, " +
                    "$FIELD_LIST_OF_NOTES TEXT NOT NULL, " +
                    "$FIELD_ADDED_DATE LONG NOT NULL " +
                    ");"
            Log.d(TAG, "create table $queryCreateTableTrack")
            sqLiteDatabase!!.execSQL(queryCreateTableTrack)

            val queryCreateTableNotes = "CREATE TABLE $TABLE_NOTES (" +
                    "$FIELD_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$FIELD_LIST_OF_NOTES TEXT NOT NULL, " +
                    "$FIELD_ORDER_NUMBER INTEGER NOT NULL , " +
                    "$FIELD_NOTE_NAME TEXT NOT NULL, " +
                    "$FIELD_FREQUENCY DOUBLE NOT NULL, " +
                    "$FIELD_STAFF_POSITION FLOAT NOT NULL, " +
                    "$FIELD_IS_HALF_TONE INTEGER DEFAULT $FALSE, " +
                    "$FIELD_AMPLITUDE DOUBLE DEFAULT 0.0, " +
                    "$FIELD_LENGTH DOUBLE DEFAULT $DEFAULT_NOTE_LENGTH, " +
                    "FOREIGN KEY ($FIELD_LIST_OF_NOTES) REFERENCES $TABLE_TRACK($FIELD_LIST_OF_NOTES)" +
                    ");"
            Log.d(TAG, "create table $queryCreateTableNotes")
            sqLiteDatabase!!.execSQL(queryCreateTableNotes)
        }
    }

    public fun saveTrack(sqLiteDatabase: SQLiteDatabase?, track: Track, trackName: String) {
        Log.d(TAG, "saving $track")

        val trackID = createTrackID(trackName)

        val trackContentValues = createTrackContentValues(track, trackName, trackID)
        val listOfNotesContentValues = createNotesListContentValues(track, trackID)

        Log.d(TAG, "insert track $trackContentValues")
        sqLiteDatabase!!.insert(TABLE_TRACK, null, trackContentValues)

        Log.d(TAG, "insert list of notes $listOfNotesContentValues")
        listOfNotesContentValues.forEach { contentValues -> sqLiteDatabase!!.insert(TABLE_NOTES, null, contentValues) }

        Log.d(TAG, "inserted track $track")
    }

    private fun createTrackID(trackName: String): String {
        return trackName + SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getCurrentLocale()).format(Date())
    }

    private fun createNotesListContentValues(track: Track, trackID: String): LinkedList<ContentValues> {
        val listOfContentValues = LinkedList<ContentValues>()

        for (pair in track.getTrack()) {
            val contentValues = ContentValues()
            contentValues.put(FIELD_LIST_OF_NOTES, trackID)
            contentValues.put(FIELD_ORDER_NUMBER, pair.first)
            contentValues.put(FIELD_NOTE_NAME, pair.second.name)
            contentValues.put(FIELD_FREQUENCY, pair.second.frequency)
            contentValues.put(FIELD_STAFF_POSITION, pair.second.staffPosition)
            contentValues.put(FIELD_IS_HALF_TONE, pair.second.isHalfTone)
            contentValues.put(FIELD_AMPLITUDE, pair.second.amplitude)
            contentValues.put(FIELD_LENGTH, pair.second.length.length)
            listOfContentValues.addFirst(contentValues)
        }
        listOfContentValues.reverse()
        return listOfContentValues
    }

    private fun getCurrentLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    private fun createTrackContentValues(track: Track, trackName: String, listID: String): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(FIELD_TRACK_NAME, trackName)
        contentValues.put(FIELD_BEATS_PER_MINUTE, track.beatsPerMinute)
        contentValues.put(FIELD_MIN_NOTE, track.minNote.length)
        contentValues.put(FIELD_MIN_NOTE_DURATION, track.minNoteDuration)
        contentValues.put(FIELD_LIST_OF_NOTES, listID)
        contentValues.put(FIELD_ADDED_DATE, Date().time)
        return contentValues
    }

    public fun listingTracks(sqLiteDatabase: SQLiteDatabase?): LinkedList<Quartet<String, Int, Long, Date>> {
        Log.d(TAG, "listingTracks")
        val tracksList = LinkedList<Quartet<String, Int, Long, Date>>()
        val cursor = sqLiteDatabase!!.query(TABLE_TRACK, arrayOf(FIELD_TRACK_NAME, FIELD_BEATS_PER_MINUTE, FIELD_MIN_NOTE, FIELD_MIN_NOTE_DURATION, FIELD_LIST_OF_NOTES, FIELD_ADDED_DATE), null, null, null, null, "$FIELD_TRACK_NAME DESC")
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(FIELD_TRACK_NAME))
            val beatsPerMinute = cursor.getInt(cursor.getColumnIndex(FIELD_BEATS_PER_MINUTE))
            val minNote = cursor.getDouble(cursor.getColumnIndex(FIELD_MIN_NOTE))
            val minDuration = cursor.getDouble(cursor.getColumnIndex(FIELD_MIN_NOTE_DURATION))
            val listOfNotesID = cursor.getString(cursor.getColumnIndex(FIELD_LIST_OF_NOTES))
            val addedDate = Date(cursor.getLong(cursor.getColumnIndex(FIELD_ADDED_DATE)))
            var duration = 0L

            val cursorList = sqLiteDatabase!!.query(TABLE_NOTES, arrayOf(FIELD_LIST_OF_NOTES, FIELD_ORDER_NUMBER, FIELD_LENGTH), "$FIELD_LIST_OF_NOTES=?", arrayOf(listOfNotesID), null, null, "$FIELD_ORDER_NUMBER DESC")
            if (cursorList.moveToNext()) {
                val lastNumber = cursorList.getInt(cursorList.getColumnIndex(FIELD_ORDER_NUMBER))
                val lastLength = cursorList.getDouble(cursorList.getColumnIndex(FIELD_LENGTH))
                duration = Math.floor(minDuration * (lastNumber + lastLength / minNote) * 1000).toLong()
            }

            tracksList.addFirst(Quartet(name, beatsPerMinute, duration, addedDate))
        }

        Log.d(TAG, "listing track $tracksList")
        return tracksList
    }

    public fun readTrack(sqLiteDatabase: SQLiteDatabase?, trackName: String, addedDate: Date): Track? {
        Log.d(TAG, "read track $trackName, $addedDate")
        val cursor = sqLiteDatabase!!.query(TABLE_TRACK, arrayOf(FIELD_TRACK_NAME, FIELD_BEATS_PER_MINUTE, FIELD_MIN_NOTE, FIELD_MIN_NOTE_DURATION, FIELD_LIST_OF_NOTES, FIELD_ADDED_DATE), "$FIELD_TRACK_NAME=? AND $FIELD_ADDED_DATE=?", arrayOf(trackName, addedDate.time.toString()), null, null, null)
        var track: Track? = null
        if (cursor.moveToNext() && cursor.count == 1) {
            val trackName = cursor.getString(cursor.getColumnIndex(FIELD_TRACK_NAME))
            val beatsPerMinute = cursor.getInt(cursor.getColumnIndex(FIELD_BEATS_PER_MINUTE))
            val minNote = cursor.getDouble(cursor.getColumnIndex(FIELD_MIN_NOTE))
            val minNoteDuration = cursor.getDouble(cursor.getColumnIndex(FIELD_MIN_NOTE_DURATION))
            val listOfNotesID = cursor.getString(cursor.getColumnIndex(FIELD_LIST_OF_NOTES))
            val addedDate = Date(cursor.getLong(cursor.getColumnIndex(FIELD_ADDED_DATE)))
            track = Track(beatsPerMinute, mapLengthToNoteLength(minNote), minNoteDuration)

            val cursorList = sqLiteDatabase!!.query(TABLE_NOTES, arrayOf(FIELD_LIST_OF_NOTES, FIELD_ORDER_NUMBER, FIELD_NOTE_NAME, FIELD_FREQUENCY, FIELD_STAFF_POSITION, FIELD_IS_HALF_TONE, FIELD_AMPLITUDE, FIELD_LENGTH), "$FIELD_LIST_OF_NOTES=?", arrayOf(listOfNotesID), null, null, "$FIELD_ORDER_NUMBER DESC")
            val listOfNotes = LinkedList<Pair<Int, Note>>()
            while (cursorList.moveToNext()) {
                val orderNumber = cursorList.getInt(cursorList.getColumnIndex(FIELD_ORDER_NUMBER))
                val noteName = cursorList.getString(cursorList.getColumnIndex(FIELD_NOTE_NAME))
                val frequency = cursorList.getDouble(cursorList.getColumnIndex(FIELD_FREQUENCY))
                val staffPosition = cursorList.getFloat(cursorList.getColumnIndex(FIELD_STAFF_POSITION))
                val isHalfTone = cursorList.getInt(cursorList.getColumnIndex(FIELD_IS_HALF_TONE)) == TRUE
                val amplitude = cursorList.getDouble(cursorList.getColumnIndex(FIELD_AMPLITUDE))
                val length = cursorList.getDouble(cursorList.getColumnIndex(FIELD_LENGTH))

                val note = Note(noteName, frequency, staffPosition, isHalfTone)
                note.amplitude = amplitude
                note.length = mapLengthToNoteLength(length)
                listOfNotes.addFirst(Pair(orderNumber, note))
            }
            track.setListOfSound(listOfNotes)
        }
        return track
    }

    private fun mapLengthToNoteLength(minNote: Double): NoteLength {
        NoteLength.values().forEach { noteLength -> if (noteLength.length == minNote) return noteLength }
        return NoteLength.FULL
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
        private val FIELD_ADDED_DATE: String = "ADDED_DATE"

        private val TABLE_NOTES: String = "TABLE_NOTES"

        private val FIELD_NOTE_NAME: String = "NOTE_NAME"
        private val FIELD_ORDER_NUMBER: String = "ORDER_NUMBER"
        private val FIELD_FREQUENCY: String = "FREQUENCY"
        private val FIELD_STAFF_POSITION: String = "STAFF_POSITION"
        private val FIELD_IS_HALF_TONE: String = "IS_HALF_TONE"
        private val FIELD_AMPLITUDE: String = "AMPLITUDE"
        private val FIELD_LENGTH: String = "LENGTH"
    }

}