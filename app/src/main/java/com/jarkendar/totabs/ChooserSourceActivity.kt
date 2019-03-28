package com.jarkendar.totabs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
                //todo info about not permission
            }
        }

        record_image.setOnClickListener {
            startActivityForResult(Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION), RECORD_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RECORD_REQUEST -> {
                audioFileUri = data!!.data
                Log.d("****", audioFileUri!!.toString())
                if (audioFileUri != null) {
                    runMusicPreview(File(audioFileUri.toString()))
                } else {
                    //todo info about problem
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkPermission(permission: String, id: Int): Boolean {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(permission),
                        id)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
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
        private val EXTENSIONS_LIST = arrayOf(".3gp", ".mp3", ".flac", ".mid", ".wav", ".ogg", ".mp4")
        private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 2
        private val RECORD_REQUEST = 3
        public val EXTRA_FILE = "extra_file"
    }
}
