package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class TrackCompressor {

    /**
     * Method delete NULL and too weak notes from track list
     */
    public fun compressTrack(track: Track) {
        val trackArray = track.getTrackArray()
        val noteToDelete = LinkedList<Pair<Int, Note>>()
        for (i in 0 until trackArray.size) {
            if (trackArray[i].second.name == NULL_NOTE || trackArray[i].second.amplitude < MIN_AMPLITUDE) {
                noteToDelete.addFirst(trackArray[i])
            }
        }
        track.getTrack().removeAll(noteToDelete)
    }

    companion object {
        private val MIN_AMPLITUDE = 50.0
        public val NULL_NOTE = "NULL"
    }
}