package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class TrackCompressor {

    public fun compressTrack(track: Track) {
        removeUnnecessarySounds(track)
    }

    /**
     * Method delete NULL and too weak notes from track list
     */
    private fun removeUnnecessarySounds(track: Track) {
        val trackArray = track.getTrackArray()
        val noteToDelete = LinkedList<Pair<Int, Note>>()
        trackArray.forEach { pair ->
            if (pair.second.name == NULL_NOTE || pair.second.amplitude < MIN_AMPLITUDE)
                noteToDelete.addFirst(pair)
        }
        track.getTrack().removeAll(noteToDelete)
    }

    companion object {
        private val MIN_AMPLITUDE = 50.0
        public val NULL_NOTE = "NULL"
    }
}