package com.jarkendar.totabs.activities

import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.draftsmen.StaffDraftsman
import com.jarkendar.totabs.draftsmen.TablatureDraftsman
import com.jarkendar.totabs.storage.TrackDatabase
import kotlinx.android.synthetic.main.activity_track.*


class TrackActivity : AppCompatActivity() {

    private lateinit var track: Track
    private lateinit var trackName: String
    private val TAG = "***trackActivity*****"

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

        tuning_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setTablatureImage(prepareTablatureImage(getNoteRadius()))
            }
        }

        setStaffImage(prepareStaffImage(radius))
        setTablatureImage(prepareTablatureImage(radius))
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
        val bitmapStaff = Bitmap.createBitmap(staff_imageView.layoutParams.width, staff_imageView.layoutParams.height, Bitmap.Config.ARGB_8888)
        val staffDraftsman = StaffDraftsman(applicationContext)
        staffDraftsman.drawTrack(bitmapStaff, track, radius)
        return bitmapStaff
    }

    private fun prepareTablatureImage(radius: Float): Bitmap {
        val bitmapTablature = Bitmap.createBitmap(tablature_imageView.layoutParams.width, tablature_imageView.layoutParams.height, Bitmap.Config.ARGB_8888)
        val tablatureDraftsman = TablatureDraftsman(applicationContext, tuning_spinner.selectedItem.toString().split(" ").toTypedArray(), 24)
        tablatureDraftsman.drawTrack(bitmapTablature, track, radius)
        return bitmapTablature
    }

    private fun setStaffImage(bitmap: Bitmap) {
        staff_imageView.setImageBitmap(bitmap)
    }

    private fun setTablatureImage(bitmap: Bitmap) {
        tablature_imageView.setImageBitmap(bitmap)
    }
}
