package com.jarkendar.totabs

import android.content.Intent
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_music_preview.*
import java.io.File

class MusicPreviewActivity : AppCompatActivity() {

    private lateinit var musicFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_preview)

        musicFile = intent.extras.getSerializable(ChooserSourceActivity.EXTRA_FILE) as File

        extractMetaData(musicFile)

        start_intent_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "audio/*"
                data = FileProvider.getUriForFile(this@MusicPreviewActivity, applicationContext.packageName + ".provider", musicFile)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                //todo info with user hasnt music player
            }
        }
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
//        Log.d("******", "${mediaFormat.getInteger("bit-rate")}")
        Log.d("******", "${mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)}")
        Log.d("******", "${mediaFormat.getLong(MediaFormat.KEY_DURATION)}")
    }
}
