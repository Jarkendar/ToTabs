package com.jarkendar.totabs.analyzer.note_parser

import java.io.Serializable

class Note constructor(val name: String, val frequency: Double, val staffPosition: Float, val isHalfTone: Boolean) : Serializable {

    var amplitude: Double = 0.0
    var length = NoteLength.FULL

    override fun toString(): String {
        return "Note(name='$name', frequency=$frequency, amplitude=$amplitude, length=${length.name}, staffPosition=${staffPosition})"
    }

    public fun copy(): Note {
        val copyNote = Note(name, frequency, staffPosition, isHalfTone)
        copyNote.amplitude = amplitude
        copyNote.length = length
        return copyNote
    }
}