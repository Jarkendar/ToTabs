package com.jarkendar.totabs.generators

import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class GriffNoteGenerator constructor(val tuning: Array<String>, val maxThreshold: Int) {

    private lateinit var griffNote: Array<Array<Pair<String, Int>>>

    init {
        val notes = NoteSpectreGenerator().getNotes()
        griffNote = Array(tuning.size) { Array(maxThreshold + 1) { Pair("", 0) } }

        (0 until tuning.size).forEach { i -> griffNote[i][0] = Pair(tuning[i], 0) }

        (0 until tuning.size).forEach { i ->
            val baseIndex = findBaseNote(tuning[i], notes)
            (1..maxThreshold).forEach { j ->
                griffNote[i][j] = Pair(notes[baseIndex + j].name, j)
            }
        }
    }

    private fun findBaseNote(baseReference: String, notes: Array<Note>): Int {
        (0 until notes.size).forEach { i -> if (notes[i].name == baseReference) return i }
        return -1
    }

    override fun toString(): String {
        var message = "GriffNoteGenerator(tuning=${Arrays.toString(tuning)}, maxThreshold=$maxThreshold, griffNote=\n"
        (0 until tuning.size).forEach { i -> message += Arrays.toString(griffNote[i]) + "\n" }
        return message
    }


}