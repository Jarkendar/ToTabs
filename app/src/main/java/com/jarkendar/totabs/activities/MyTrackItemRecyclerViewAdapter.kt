package com.jarkendar.totabs.activities

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jarkendar.totabs.R


import com.jarkendar.totabs.activities.TrackItemFragment.OnListFragmentInteractionListener
import com.jarkendar.totabs.analyzer.note_parser.Quartet

import kotlinx.android.synthetic.main.fragment_trackitem.view.*
import java.text.SimpleDateFormat
import java.util.*

class MyTrackItemRecyclerViewAdapter(
        private val context: Context,
        private val mValues: LinkedList<Quartet<String, Int, Long, Date>>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyTrackItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Quartet<String, Int, Long, Date>
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_trackitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.quartet = item
        holder.mTrackName.text = item.first
        holder.mBeatsPerMinute.text = "${context.getText(R.string.prefix_beats_per_minute_text)} ${item.second}"
        holder.mDuration.text = "${context.getText(R.string.prefix_duration_text)} ${createTimeString(item.third)}"
        holder.mAddedDate.text = "${context.getText(R.string.prefix_added_date_text)} ${SimpleDateFormat("HH:mm dd.MM.yyyy", getCurrentLocale()).format(item.fourth)}"

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    private fun createTimeString(time: Long): String {
        val minutes = time / (60 * 1000)
        val seconds = time % (60 * 1000) / 1000
        val milliseconds = time % 1000
        return String.format("%01d:%02d.%01d", minutes, seconds, milliseconds)
    }

    private fun getCurrentLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mTrackName: TextView = mView.textView_track_name
        var mBeatsPerMinute: TextView = mView.textView_bpm
        var mDuration: TextView = mView.textView_duration
        var mAddedDate: TextView = mView.textView_added_date
        lateinit var quartet: Quartet<String, Int, Long, Date>

        override fun toString(): String {
            return "ViewHolder(mView=$mView, mTrackName=$mTrackName, mBeatsPerMinute=$mBeatsPerMinute, mDuration=$mDuration, mAddedDate=$mAddedDate, quartet=$quartet)"
        }


    }
}
