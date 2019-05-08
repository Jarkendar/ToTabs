package com.jarkendar.totabs.activities

import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.draftsmen.StaffDraftsman
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
        val radius = getNoteRadius()
        setWidthOfImagesViews(radius, track)

        setStaffImage(prepareStaffImage(radius))
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
}
