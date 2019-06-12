package com.jarkendar.totabs.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.activities.chooser.DialogCreator
import com.jarkendar.totabs.analyzer.Analyzer
import com.jarkendar.totabs.analyzer.MusicFileHolder
import com.jarkendar.totabs.analyzer.Track
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
                DialogCreator(this).createDialog(applicationContext.getString(R.string.title_text), applicationContext.getString(R.string.phone_has_none_music_player_text), applicationContext.getString(R.string.understand_accept_button)).show()
            }
        }
        start_analyze_button.setOnClickListener {
            //todo start analyze
            val analyzer = Analyzer(musicFileHolder)
            analyzer.beatsPerMinute = beats_per_minute_editText.text.toString().toInt()
            val analyzerThread = AnalyzerThread(this, analyzer)
            analyzerThread.execute()
            Log.d("*******", "Click analyze button")
        }

        beats_per_minute_editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty() && s.all { it.toString().contains(Regex("[0-9]")) }) {
                    val bpm = s.toString().toInt()
                    if (bpm in MIN_BEATS_PER_MINUTE..MAX_BEATS_PER_MINUTE) {
                        beats_per_minute_editText.setTextColor(resources.getColor(R.color.green))
                        start_analyze_button.isEnabled = true
                    } else {
                        beats_per_minute_editText.setTextColor(resources.getColor(R.color.red))
                        start_analyze_button.isEnabled = false
                    }
                } else {
                    beats_per_minute_editText.setTextColor(resources.getColor(R.color.red))
                    start_analyze_button.isEnabled = false
                }
            }
        })
    }

    fun disableViews() {
        start_analyze_button.isEnabled = false
        beats_per_minute_editText.isEnabled = false
    }

    fun enableViews() {
        start_analyze_button.isEnabled = true
        beats_per_minute_editText.isEnabled = true
    }

    private fun startTrackActivity(track: Track) {
        val intent = Intent(this, TrackActivity::class.java)
        intent.putExtra(TRACK_EXTRA_NAME, track)
        intent.putExtra(TRACK_NAME, musicFileHolder.name)
        startActivity(intent)
    }

    private class AnalyzerThread constructor(val musicPreviewActivity: MusicPreviewActivity, val analyzer: Analyzer) : AsyncTask<Void, Void, String>() {
        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            //todo progress bar
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            musicPreviewActivity.enableViews()
            musicPreviewActivity.startTrackActivity(analyzer.getTrack())
            //todo hide progress bar
        }

        override fun doInBackground(vararg params: Void?): String {
            analyzer.analyze()
            return ""//todo return result from analyzer
        }

        override fun onPreExecute() {
            super.onPreExecute()
            musicPreviewActivity.disableViews()
            //todo show progress bar
            //todo
        }
    }

    companion object {
        private const val MIN_BEATS_PER_MINUTE = 1
        private const val MAX_BEATS_PER_MINUTE = 240
        public const val TRACK_EXTRA_NAME = "TRACK"
        public const val TRACK_NAME = "TRACK_NAME"
    }
}
