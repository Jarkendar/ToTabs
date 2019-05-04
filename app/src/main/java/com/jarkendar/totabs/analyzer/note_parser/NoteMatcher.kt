package com.jarkendar.totabs.analyzer.note_parser

import android.util.Log
import java.util.*
import kotlin.collections.HashMap

class NoteMatcher {

    private var noteSpectreGenerator: NoteSpectreGenerator = NoteSpectreGenerator()

    public fun match(recognizeFrequencies: Array<Pair<Double, Double>>): Note {
        //todo match to chords, generalize note to sound

        val notes = Array(recognizeFrequencies.size) { Note("null", 0.0) }
        for (i in 0 until recognizeFrequencies.size) {
            notes[i] = chooseLowestSEFromNotes(recognizeFrequencies[i])
        }

        Log.d("******", Arrays.toString(notes))
        val joinedNotes = joinTheSameNotes(notes)
        Log.d("******", Arrays.toString(joinedNotes))

        return joinedNotes[0]
    }

    private fun joinTheSameNotes(array: Array<Note>): Array<Note> {
        val list = LinkedList<Note>()
        val indexes = HashMap<String, LinkedList<Int>>()
        for (i in 0 until array.size) {
            if (indexes.containsKey(array[i].name)) {
                indexes[array[i].name]!!.addFirst(i)
            } else {
                indexes[array[i].name] = LinkedList()
                indexes[array[i].name]!!.addFirst(i)
            }
        }

        for ((key, value) in indexes) {
            var sumAmplitude = 0.0
            val frequency = array[value[0]].frequency
            for (i in value) {
                sumAmplitude += array[i].amplitude

            }
            val note = Note(key, frequency)
            note.amplitude = sumAmplitude
            list.addLast(note)
        }
        sortNotesByAmplitude(list)
        return list.toTypedArray()
    }

    private fun sortNotesByAmplitude(notes: LinkedList<Note>) {
        notes.sortWith(kotlin.Comparator { o1, o2 -> -compareValues(o1.amplitude, o2.amplitude) })
    }

    private fun chooseLowestSEFromNotes(reference: Pair<Double, Double>): Note {
        val notes = noteSpectreGenerator.getNotes()

        var minSE = Double.MAX_VALUE
        var minSEIndex = -1
        val lowerBound = reference.first * 0.75
        val upperBound = reference.first * 1.25
        for (i in 0 until notes.size) {
            if (notes[i].frequency < lowerBound) {
                continue
            } else if (notes[i].frequency > upperBound) {
                break
            }
            val squareError = Math.pow((reference.first - notes[i].frequency), 2.0)
            if (minSE > squareError) {
                minSE = squareError
                minSEIndex = i
            }
        }
        if (minSEIndex == -1) {
            return Note("null", 0.0)
        }
        val matchNote = notes[minSEIndex].copy()
        matchNote.amplitude = reference.second
        return matchNote
    }
}