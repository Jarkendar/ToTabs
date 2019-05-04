package com.jarkendar.totabs.analyzer

import com.jarkendar.totabs.analyzer.note_parser.Note
import com.jarkendar.totabs.analyzer.note_parser.NoteLength
import java.util.*
import kotlin.math.abs

class TrackCompressor {

    public fun compressTrack(track: Track) {
        removeUnnecessarySounds(track)
        joinSounds(track)
    }

    /**
     * Method delete NULL and too weak notes from track list
     */
    private fun removeUnnecessarySounds(track: Track) {
        val trackArray = track.getTrackArray()
        val noteToDelete = LinkedList<Pair<Int, Note>>()
        trackArray.forEach { pair ->
            if (pair.second.name == NULL_NOTE || pair.second.amplitude < MIN_AMPLITUDE)
                noteToDelete.addFirst(pair)
        }
        track.getTrack().removeAll(noteToDelete)
    }

    private fun joinSounds(track: Track) {
        val trackArray = track.getTrackArray()
        val joinedTruck = LinkedList<Pair<Int, Note>>()

        val maxJoining = (1.0 / track.minNote.length).toInt()
        val joinOptions = prepareJoinOptions(maxJoining)

        var currentIndex = 0
        while (currentIndex < trackArray.size) {
            var joined = false
            for (option in joinOptions) {
                if (trackArray.size - currentIndex < option) {
                    continue
                }
                if (canJoin(trackArray, option, currentIndex)) {
                    joinedTruck.addLast(joinNotes(trackArray, option, currentIndex))
                    currentIndex += option
                    joined = true
                    break
                } else {
                    break
                }
            }
            if (!joined) {
                joinedTruck.addLast(trackArray[currentIndex])
                currentIndex++
            }
        }
        track.setListOfSound(joinedTruck)
    }

    private fun prepareJoinOptions(joining: Int): Array<Int> {
        var joining = joining
        val numbers = LinkedList<Int>()
        while (joining > 1) {
            numbers.addLast(joining)
            joining /= 2
        }
        return numbers.toTypedArray()
    }

    private fun canJoin(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        return when {
            checkIndexesAreNotOrdered(array, toJoin, startPoint) -> false
            checkNamesAreDifference(array, toJoin, startPoint) -> false
            checkLengthAreDifference(array, toJoin, startPoint) -> false
            checkAmplitudeIsMonotonic(array, toJoin, startPoint) -> true
            else -> false
        }
    }

    private fun checkIndexesAreNotOrdered(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        return (1 until toJoin).any { i -> array[startPoint + i - 1].first + 1 != array[startPoint + i].first }
    }

    private fun checkNamesAreDifference(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        val referenceName = array[startPoint].second.name
        return (1 until toJoin).any { i -> array[startPoint + i].second.name != referenceName }
    }

    private fun checkLengthAreDifference(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        val referenceLength = array[startPoint].second.length
        return (1 until toJoin).any { i -> array[startPoint + i].second.length != referenceLength }
    }

    private fun checkAmplitudeIsMonotonic(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        return (1 until toJoin).all { i ->
            array[startPoint + i - 1].second.amplitude >= array[startPoint + i].second.amplitude ||
                    abs(array[startPoint + i - 1].second.amplitude - array[startPoint + i].second.amplitude) <= array[startPoint + i - 1].second.amplitude * AMPLITUDE_MARGIN
        }
    }

    private fun joinNotes(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Pair<Int, Note> {
        val index = array[startPoint].first
        val name = array[startPoint].second.name
        val frequency = array[startPoint].second.frequency

        var sumAmplitudes = 0.0
        (0 until toJoin).forEach { i -> sumAmplitudes += array[startPoint + i].second.amplitude }

        val note = Note(name, frequency)
        note.amplitude = sumAmplitudes
        note.length = calcNoteLength(toJoin, array[startPoint].second.length)
        return Pair(index, note)
    }

    private fun calcNoteLength(toJoin: Int, beforeLength: NoteLength): NoteLength {
        val afterLength = beforeLength.length * toJoin
        NoteLength.values().forEach { noteLength -> if (noteLength.length == afterLength) return noteLength }
        return beforeLength
    }


    companion object {
        private const val MIN_AMPLITUDE = 50.0
        public const val NULL_NOTE = "NULL"
        private const val AMPLITUDE_MARGIN = 0.05
    }
}