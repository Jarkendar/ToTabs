package com.jarkendar.totabs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.io.File

class MusicPreviewActivity : AppCompatActivity() {

    private lateinit var musicFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_preview)

        musicFile = intent.extras.getSerializable(ChooserSourceActivity.EXTRA_FILE) as File
    }
}
