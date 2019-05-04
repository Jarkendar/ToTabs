package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import com.jarkendar.totabs.analyzer.note_parser.NoteLength
import java.util.*

class Track(val beatsPerMinute: Int, val minNote: NoteLength, val minNoteDuration: Double) {

    private val listOfSound = LinkedList<Pair<Int, Note>>()

    public fun appendSound(note: Note) {
        note.length = minNote
        listOfSound.addLast(Pair(listOfSound.size, note))
    }

    public fun getTrack(): LinkedList<Pair<Int, Note>> {
        return listOfSound
    }

    public fun getTrackArray(): Array<Pair<Int, Note>> {
        return listOfSound.toTypedArray()
    }

    override fun toString(): String {
        return "Track(beatsPerMinute=$beatsPerMinute, ${minNote.name}, $minNoteDuration, ${listOfSound.size}, listOfSound=${Arrays.toString(listOfSound.toTypedArray())})"
    }
}