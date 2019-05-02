package com.jarkendar.totabs.analyzer.note_parser

class Note constructor(val name: String, val frequency: Double) {

    override fun toString(): String {
        return "Note(name='$name', frequency=$frequency)"
    }
}