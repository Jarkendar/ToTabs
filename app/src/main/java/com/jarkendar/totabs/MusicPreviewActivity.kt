package com.jarkendar.totabs

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_music_preview.*
import java.io.File

class MusicPreviewActivity : AppCompatActivity() {

    private lateinit var musicFileHolder: MusicFileHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_preview)

        musicFileHolder = MusicFileHolder(intent.extras.getSerializable(ChooserSourceActivity.EXTRA_FILE) as File, applicationContext)

        basic_info_textView.text = musicFileHolder.getBasicInfo()
        advance_info_textView.text = musicFileHolder.getAdvanceInfo()

        start_intent_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "audio/*"
                data = FileProvider.getUriForFile(this@MusicPreviewActivity, applicationContext.packageName + ".provider", musicFileHolder.musicFile)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                //todo info with user hasnt music player
            }
        }
        start_analyze_button.setOnClickListener {
            //todo start analyze
            Log.d("*******", "Click analyze button")
        }
    }
}
