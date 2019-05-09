package com.jarkendar.totabs.analyzer.note_parser

enum class NoteLength(val length: Double, val fill: Boolean, val column: Boolean, val numberOfTails: Int) {
    FULL(4.0, false, false, 0),
    HALF(2.0, false, true, 0),
    QUARTER(1.0, true, true, 0),//is base
    EIGHTH(0.5, true, true, 1),
    SIXTEENTH(0.25, true, true, 2),
    THIRTY_SECOND(0.125, true, true, 3)
}