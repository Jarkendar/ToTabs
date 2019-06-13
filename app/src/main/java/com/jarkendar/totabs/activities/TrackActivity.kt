package com.jarkendar.totabs.activities

import android.graphics.Bitmap
import android.graphics.Point
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.draftsmen.StaffDraftsman
import com.jarkendar.totabs.storage.TrackDatabase
import kotlinx.android.synthetic.main.activity_track.*


class TrackActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var trackName: String
    private val TAG = "***trackActivity*****"
    private lateinit var audioTrack: AudioTrack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        track = intent.extras.getSerializable(MusicPreviewActivity.TRACK_EXTRA_NAME) as Track
        Log.d(TAG, track.toString())
        trackName = intent.extras.getString(MusicPreviewActivity.TRACK_NAME)
        Log.d(TAG, trackName)

        setHeightOfImagesViews()
        val radius = getNoteRadius()
        setWidthOfImagesViews(radius, track)

        save_button.setOnClickListener { v: View? ->
            //todo check in database if exist show info
            val trackDatabase = TrackDatabase(applicationContext)
            synchronized(applicationContext) {
                trackDatabase.saveTrack(trackDatabase.writableDatabase, track, trackName)
            }
        }

        play_button.setOnClickListener { v: View? ->
            play_button.isClickable = false
            val soundData = createMusicTrack(track)
            audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_DEFAULT,
                    AudioFormat.ENCODING_PCM_8BIT, soundData.size,
                    AudioTrack.MODE_STATIC
            )
            audioTrack.write(soundData, 0, soundData.size)
            audioTrack.play()
        }

        setStaffImage(prepareStaffImage(radius))
    }

    private fun createMusicTrack(track: Track): ByteArray {
        val duration = Math.ceil(track.minNoteDuration * (track.getTrack().last.first + track.getTrack().last.second.length.length / track.minNote.length)).toInt()
        val byteArray = ByteArray(SAMPLE_RATE * duration) { 0 }

        val quarterNoteTime = track.minNoteDuration / track.minNote.length
        Log.d(TAG, "create music $duration, ${byteArray.size}, $quarterNoteTime")
        var i = 0
        for (pair in track.getTrack()) {
            for (sample in 0..(quarterNoteTime * pair.second.length.length * SAMPLE_RATE).toInt()) {
                val freqMultiplySample = pair.second.frequency * sample
                byteArray[i++] = ((Math.sin(freqMultiplySample * TWO_PI_DIV_SAMPLE_FIRST)
                        + Math.sin(freqMultiplySample * TWO_PI_DIV_SAMPLE_SECOND) / 2
                        + Math.sin(freqMultiplySample * TWO_PI_DIV_SAMPLE_THIRD) / 3
                        + Math.sin(freqMultiplySample * TWO_PI_DIV_SAMPLE_FOURTH) / 4)
                        * 255).toByte()
            }
        }
        Log.d(TAG, "end create music $i")
        return byteArray
    }

    private fun setHeightOfImagesViews() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        val screenHeight = size.y
        val height = ((screenHeight - ((3 + 4) * resources.getDimension(R.dimen.margin_8dp) + resources.getDimension(R.dimen.button_height))) / 2.2).toInt()
        staff_imageView.layoutParams.height = height
        tablature_imageView.layoutParams.height = height
    }

    private fun setWidthOfImagesViews(noteRadius: Float, track: Track) {
        val width = ((StaffDraftsman.FIRST_NOTE_POSITION + track.getTrack().size * StaffDraftsman.ONE_NOTE_AREA + StaffDraftsman.SUFFIX) * noteRadius).toInt()
        staff_imageView.layoutParams.width = width
        tablature_imageView.layoutParams.width = width
    }

    private fun getNoteRadius(): Float {
        return staff_imageView.layoutParams.height.toFloat() / (StaffDraftsman.MAX_LINES_ON_STAFF * 2)
    }

    private fun prepareStaffImage(radius: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(staff_imageView.layoutParams.width, staff_imageView.layoutParams.height, Bitmap.Config.ARGB_8888)
        val staffDraftsman = StaffDraftsman(applicationContext)
        staffDraftsman.drawTrack(bitmap, track, radius)
        return bitmap
    }

    private fun setStaffImage(bitmap: Bitmap) {
        staff_imageView.setImageBitmap(bitmap)
    }

    companion object {
        private val SAMPLE_RATE = 44100
        private val TWO_PI_DIV_SAMPLE_FIRST = 2 * Math.PI / SAMPLE_RATE
        private val TWO_PI_DIV_SAMPLE_SECOND = 2 * Math.PI / SAMPLE_RATE * 2
        private val TWO_PI_DIV_SAMPLE_THIRD = 2 * Math.PI / SAMPLE_RATE * 3
        private val TWO_PI_DIV_SAMPLE_FOURTH = 2 * Math.PI / SAMPLE_RATE * 4
    }
}
