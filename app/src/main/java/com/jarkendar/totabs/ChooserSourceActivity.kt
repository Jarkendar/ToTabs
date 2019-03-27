package com.jarkendar.totabs

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_chooser_source.*
import java.io.File

class ChooserSourceActivity : AppCompatActivity(), FileChooser.FileSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser_source)

        choose_file_image.setOnClickListener {
            val fileChooser = FileChooser(this@ChooserSourceActivity)
            fileChooser.setFileListener(this).showDialog()
        }
    }

    override fun fileSelected(file: File) {
        Log.d("*****", file.absolutePath)
        Log.d("*****", file.name)
        Log.d("*****", file.length().toString())
    }
}
