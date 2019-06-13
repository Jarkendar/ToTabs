package com.jarkendar.totabs.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.note_parser.Quartet
import java.util.*

class LibraryActivity : AppCompatActivity(), TrackItemFragment.OnListFragmentInteractionListener {

    private lateinit var trackItemFragment: TrackItemFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        if (savedInstanceState == null) {
            trackItemFragment = TrackItemFragment.newInstance(2)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, trackItemFragment)
                    .commitNow()
        }
    }

    override fun onListFragmentInteraction(item: Quartet<String, Int, Long, Date>?) {
        Log.d(TAG, "List on click $item")
    }

    companion object {
        private val TAG = "*******"
    }
}
