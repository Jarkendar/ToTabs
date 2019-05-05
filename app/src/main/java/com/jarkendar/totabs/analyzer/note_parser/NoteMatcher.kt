package com.jarkendar.totabs.analyzer.note_parser

import android.util.Log
import com.jarkendar.totabs.analyzer.TrackCompressor
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

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
            if (!indexes.containsKey(array[i].name)) {
                indexes[array[i].name] = LinkedList()
            }
            indexes[array[i].name]!!.addFirst(i)
        }

        for ((name, linkedList) in indexes) {
            var sumAmplitudes = 0.0
            val frequency = array[linkedList.first].frequency
            for (index in linkedList) {
                sumAmplitudes += array[index].amplitude
            }
            val note = Note(name, frequency)
            note.amplitude = sumAmplitudes
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

        var minError = Double.MAX_VALUE
        var minErrorIndex = -1
        val lowerBound = reference.first * LOWER_BOUND_MULTIPLIER
        val upperBound = reference.first * UPPER_BOUND_MULTIPLIER
        for (i in 0 until notes.size) {
            if (notes[i].frequency < lowerBound) {
                continue
            } else if (notes[i].frequency > upperBound) {
                break
            }
            val error = abs(reference.first - notes[i].frequency)
            if (minError > error) {
                minError = error
                minErrorIndex = i
            }
        }
        if (minErrorIndex == -1) {
            return Note(TrackCompressor.NULL_NOTE, 0.0)
        }
        val matchNote = notes[minErrorIndex].copy()
        matchNote.amplitude = reference.second
        return matchNote
    }

    companion object {
        private const val LOWER_BOUND_MULTIPLIER = 0.75
        private const val UPPER_BOUND_MULTIPLIER = 1.25
    }
}