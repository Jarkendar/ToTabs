package com.jarkendar.totabs.analyzer.note_parser

enum class NoteLength(val length: Double) {
    FULL(4.0),
    HALF(2.0),
    QUARTER(1.0),//is base
    EIGHTH(0.5),
    SIXTEENTH(0.25),
    THIRTY_SECOND(0.125)
}