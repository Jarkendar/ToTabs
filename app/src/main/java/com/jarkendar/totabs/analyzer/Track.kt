package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import com.jarkendar.totabs.analyzer.note_parser.NoteLength
import java.util.*

class Track(val beatsPerMinute: Int, val minNote: NoteLength, val minNoteDuration: Double) {

    private val listOfSound = LinkedList<Note>()

    public fun appendSound(note: Note) {
        note.length = minNote
        listOfSound.addLast(note)
    }

    public fun getTrack(): Array<Note> {
        return listOfSound.toTypedArray()
    }

    override fun toString(): String {
        return "Track(beatsPerMinute=$beatsPerMinute, ${minNote.name}, $minNoteDuration, listOfSound=${Arrays.toString(listOfSound.toTypedArray())})"
    }
}