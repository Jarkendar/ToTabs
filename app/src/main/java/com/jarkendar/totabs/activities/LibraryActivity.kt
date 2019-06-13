package com.jarkendar.totabs.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.analyzer.note_parser.Quartet
import com.jarkendar.totabs.storage.TrackDatabase
import java.util.*

class LibraryActivity : AppCompatActivity(), TrackItemFragment.OnListFragmentInteractionListener {

    private lateinit var trackItemFragment: TrackItemFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        if (savedInstanceState == null) {
            trackItemFragment = TrackItemFragment.newInstance(2)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, trackItemFragment)
                    .commitNow()
        }
    }

    override fun onListFragmentInteraction(quartet: Quartet<String, Int, Long, Date>?) {
        Log.d(TAG, "List on click $quartet")
        val trackName = quartet!!.first
        val trackDatabase = TrackDatabase(applicationContext)
        var track: Track? = null
        synchronized(applicationContext) {
            track = trackDatabase.readTrack(trackDatabase.readableDatabase, quartet.first, quartet.fourth)
            Log.d(TAG, "readed track $track")
        }
        if (track != null) {
            val intent = Intent(applicationContext, TrackActivity::class.java)
            intent.putExtra(MusicPreviewActivity.TRACK_EXTRA_NAME, track)
            intent.putExtra(MusicPreviewActivity.TRACK_NAME, trackName)
            startActivity(intent)
        } else {
            //todo info to user
        }
    }

    companion object {
        private val TAG = "*******"
    }
}
