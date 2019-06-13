package com.jarkendar.totabs.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.jarkendar.totabs.R
import com.jarkendar.totabs.activities.chooser.DialogCreator
import com.jarkendar.totabs.activities.chooser.FileChooser
import kotlinx.android.synthetic.main.activity_chooser_source.*
import java.io.File


class ChooserSourceActivity : AppCompatActivity(), FileChooser.FileSelectedListener {

    private var audioFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_source)

        choose_file_image.setOnClickListener {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_EXTERNAL_STORAGE)) {
                val fileChooser = FileChooser(this@ChooserSourceActivity)
                fileChooser.setExtensions(EXTENSIONS_LIST)
                fileChooser.setFileListener(this).showDialog()
            } else {
                showNotPermissionDialog()
            }
        }

        record_image.setOnClickListener {
            startActivityForResult(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), RECORD_REQUEST)
        }
    }

    private fun showNotPermissionDialog() {
        DialogCreator(this).createDialog(applicationContext.getString(R.string.dialog_info_title_text), applicationContext.getString(R.string.not_write_external_permission_text), applicationContext.getString(R.string.understand_accept_button)).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RECORD_REQUEST -> {
                audioFileUri = data!!.data
                if (audioFileUri != null) {
                    runMusicPreview(File(getRealPathFromURI(audioFileUri!!)))
                } else {
                    DialogCreator(this).createDialog(applicationContext.getString(R.string.dialog_info_title_text), applicationContext.getString(R.string.problem_with_record_file_text), applicationContext.getString(R.string.understand_accept_button)).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun checkPermission(permission: String, id: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                DialogCreator(this).createDialog(applicationContext.getString(R.string.dialog_info_title_text), applicationContext.getString(R.string.why_need_write_external_storage_text), applicationContext.getString(R.string.understand_accept_button))
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), id)
            }
        } else {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    choose_file_image.callOnClick()
                } else {
                    showNotPermissionDialog()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun fileSelected(file: File) {
        runMusicPreview(file)
    }

    private fun runMusicPreview(file: File) {
        val intent = Intent(this, MusicPreviewActivity::class.java)
        intent.putExtra(EXTRA_FILE, file)
        startActivity(intent)
    }

    companion object {
        private val EXTENSIONS_LIST = arrayOf(".wav")//arrayOf(".3gp", ".mp3", ".flac", ".mid", ".wav", ".ogg", ".mp4")
        private const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 2
        private const val RECORD_REQUEST = 3
        public const val EXTRA_FILE = "extra_file"
    }
}
