package com.jarkendar.totabs

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.File

class MusicPreviewActivity : AppCompatActivity() {

    private lateinit var musicFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_preview)

        musicFile = intent.extras.getSerializable(ChooserSourceActivity.EXTRA_FILE) as File

        extractMetaData(musicFile)
    }

    private fun extractMetaData(file: File) {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(file.absolutePath)
        Log.d("******", mediaExtractor.toString())
        Log.d("******", mediaExtractor.trackCount.toString())
        Log.d("******", mediaExtractor.sampleTime.toString())
        Log.d("******", mediaExtractor.cachedDuration.toString())
        val mediaFormat = mediaExtractor.getTrackFormat(0)
        Log.d("******", mediaFormat.toString())
        Log.d("******", mediaFormat.getString(MediaFormat.KEY_MIME))

//        Log.d("******", mediaFormat.getInteger(MediaFormat.KEY_BITRATE_MODE).toString())
        Log.d("******", "${mediaFormat.getInteger("bit-rate")}")
        Log.d("******", "${mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)}")
        Log.d("******", "${mediaFormat.getLong(MediaFormat.KEY_DURATION)}")
    }
}
