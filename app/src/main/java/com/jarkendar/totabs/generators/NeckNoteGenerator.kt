package com.jarkendar.totabs.generators

import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class NeckNoteGenerator constructor(val tuning: Array<String>, val maxFret: Int) {

    private lateinit var neckNote: Array<Array<Pair<String, Int>>>

    init {
        val notes = NoteSpectreGenerator().getNotes()
        neckNote = Array(tuning.size) { Array(maxFret + 1) { Pair("", 0) } }

        (0 until tuning.size).forEach { i -> neckNote[i][0] = Pair(tuning[i], 0) }

        (0 until tuning.size).forEach { i ->
            val baseIndex = findBaseNote(tuning[i], notes)
            (1..maxFret).forEach { j ->
                neckNote[i][j] = Pair(notes[baseIndex + j].name, j)
            }
        }
    }

    private fun findBaseNote(baseReference: String, notes: Array<Note>): Int {
        (0 until notes.size).forEach { i -> if (notes[i].name == baseReference) return i }
        return -1
    }

    override fun toString(): String {
        var message = "NeckNoteGenerator(tuning=${Arrays.toString(tuning)}, maxFret=$maxFret, neckNote=\n"
        (0 until tuning.size).forEach { i -> message += Arrays.toString(neckNote[i]) + "\n" }
        return message
    }


}