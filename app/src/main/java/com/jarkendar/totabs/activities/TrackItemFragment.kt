package com.jarkendar.totabs.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jarkendar.totabs.R

import com.jarkendar.totabs.analyzer.note_parser.Quartet
import com.jarkendar.totabs.storage.TrackDatabase
import java.util.*


class TrackItemFragment : Fragment() {

    private var columnCount = 2

    private var listener: OnListFragmentInteractionListener? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trackitem_list, container, false)

        if (view is RecyclerView) {
            recyclerView = view
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val trackDatabase = TrackDatabase(context)
                adapter = MyTrackItemRecyclerViewAdapter(context, trackDatabase.listingTracks(trackDatabase.readableDatabase), listener)
            }
        }
        return view
    }

    public fun refreshAdapter() {
        (recyclerView!!.adapter as MyTrackItemRecyclerViewAdapter).setList(readTaskList())
        recyclerView!!.adapter.notifyDataSetChanged()
    }

    private fun readTaskList(): LinkedList<Quartet<String, Int, Long, Date>> {
        val trackDatabase = TrackDatabase(context!!)
        return trackDatabase.listingTracks(trackDatabase.readableDatabase)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Quartet<String, Int, Long, Date>?)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                TrackItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
