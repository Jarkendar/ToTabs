package com.jarkendar.totabs.analyzer.note_parser

class Note constructor(val name: String, val frequency: Double) {

    var amplitude: Double = 0.0
    var length = NoteLength.FULL

    override fun toString(): String {
        return "Note(name='$name', frequency=$frequency, amplitude=$amplitude, length=${length.name})"
    }

    public fun copy(): Note {
        return Note(name, frequency)
    }
}