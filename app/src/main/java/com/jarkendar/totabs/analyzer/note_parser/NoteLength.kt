package com.jarkendar.totabs.analyzer.note_parser

enum class NoteLength(val length: Double) {
    FULL(1.0),
    HALF(0.5),
    QUARTER(0.25),
    EIGHTH(0.125),
    SIXTEENTH(0.0625),
    THIRTY_SECOND(0.03125)
}