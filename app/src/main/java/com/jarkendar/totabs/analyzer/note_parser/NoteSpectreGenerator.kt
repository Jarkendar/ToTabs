package com.jarkendar.totabs.analyzer.note_parser

import java.util.*

class NoteSpectreGenerator {

    private val notes = LinkedList<Note>()

    init {
        val octaveSize = Octave.values().size
        val baseFrequencies = DoubleArray(octaveSize)
        val baseNames = Array(octaveSize) { "" }

        for ((i, note) in Octave.values().iterator().withIndex()) {
            notes.add(Note(note.name, note.baseValue))
            baseNames[i] = note.string
            baseFrequencies[i] = note.baseValue
        }

        var currentTone: Double
        var i = octaveSize
        do {
            currentTone = baseFrequencies[i % octaveSize] * Math.pow(2.0, (i / octaveSize).toDouble()).toInt()
            val nextName = generateToneName(baseNames[i % octaveSize], i / octaveSize + 1)
            notes.add(Note(nextName, currentTone))
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