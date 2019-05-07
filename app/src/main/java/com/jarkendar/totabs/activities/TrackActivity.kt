package com.jarkendar.totabs.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track

class TrackActivity : AppCompatActivity() {

    private lateinit var track: Track
    private val TAG = "***trackActivity*****"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        track = intent.extras.getSerializable(MusicPreviewActivity.TRACK_EXTRA_NAME) as Track
        Log.d(TAG, track.toString())
    }
}
