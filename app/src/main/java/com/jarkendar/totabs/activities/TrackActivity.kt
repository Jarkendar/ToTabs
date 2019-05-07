package com.jarkendar.totabs.activities

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import kotlinx.android.synthetic.main.activity_track.*


class TrackActivity : AppCompatActivity() {

    private lateinit var track: Track
    private val TAG = "***trackActivity*****"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        track = intent.extras.getSerializable(MusicPreviewActivity.TRACK_EXTRA_NAME) as Track
        Log.d(TAG, track.toString())

        setHeightOfImagesViews()
    }

    private fun setHeightOfImagesViews() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val screenHeight = size.y
        val height = ((screenHeight - ((3 + 4) * resources.getDimension(R.dimen.margin_8dp) + resources.getDimension(R.dimen.button_height))) / 2.2).toInt()
        staff_imageView.layoutParams.height = height
        tablature_imageView.layoutParams.height = height
    }
}
