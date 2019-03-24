package com.jarkendar.totabs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_chooser_source.*

class ChooserSourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_source)

        choose_file_image.setOnClickListener {
            val fileChooser = FileChooser(this@ChooserSourceActivity)
            fileChooser.setFileListener {
                Log.d("*****", it.absolutePath)
                Log.d("*****", it.name)
                Log.d("*****", it.totalSpace.toString())
            }.showDialog()
        }
    }
}
