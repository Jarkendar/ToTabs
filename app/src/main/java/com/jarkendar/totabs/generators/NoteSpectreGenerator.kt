package com.jarkendar.totabs.generators

import com.jarkendar.totabs.Quartet
import com.jarkendar.totabs.analyzer.note_parser.Note
import java.util.*

class NoteSpectreGenerator {

    private val notes = LinkedList<Note>()

    init {
        val octaveSize = Octave.values().size
        val baseOctave = Array(octaveSize) { Quartet("", 0.0, 0.0f, false) }

        for ((i, note) in Octave.values().iterator().withIndex()) {
            notes.add(Note(note.name, note.baseValue, note.staffPosition, note.isHalfTone))
            baseOctave[i] = Quartet(note.string, note.baseValue, note.staffPosition, note.isHalfTone)
        }

        var currentTone: Double
        var i = octaveSize
        do {
            currentTone = baseOctave[i % octaveSize].second * Math.pow(2.0, (i / octaveSize).toDouble()).toInt()
            val nextName = generateToneName(baseOctave[i % octaveSize].first, i / octaveSize + 1)
            val position = baseOctave[i % octaveSize].third + (i / octaveSize) * 3.5f
            notes.add(Note(nextName, currentTone, position, baseOctave[i % octaveSize].fourth))
            i++
        } while (currentTone < MAX_TONE_FREQUENCY)
    }

    /**
     * Octave number compatible with the marking from : http://www.michalkaszczyszyn.com/pl/lessons/notes.html
     */
    private fun generateToneName(baseName: String, octaveNumber: Int): String {
        return when {
            octaveNumber == 2 -> baseName.toLowerCase()
            octaveNumber > 2 -> baseName.toLowerCase().plus((octaveNumber - 2).toString())
            else -> baseName
        }
    }

    public fun getNotes(): Array<Note> {
        return notes.toTypedArray()
    }

    companion object {
        private const val MAX_TONE_FREQUENCY = 2000.0
    }
}