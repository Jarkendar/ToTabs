package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class Track(val beatsPerMinute: Int, val minNote: Double, val minNoteDuration: Double) {

    private val listOfSound = LinkedList<Note>()

    public fun appendSound(note: Note) {
        listOfSound.addLast(note)
    }

    public fun getTrack(): Array<Note> {
        return listOfSound.toTypedArray()
    }

    override fun toString(): String {
        return "Track(beatsPerMinute=$beatsPerMinute, listOfSound=${Arrays.toString(listOfSound.toTypedArray())})"
    }
}