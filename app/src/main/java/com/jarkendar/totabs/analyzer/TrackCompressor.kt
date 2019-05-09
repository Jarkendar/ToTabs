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
        trackArray
                .filter { pair -> pair.second.name == NULL_NOTE || pair.second.amplitude < MIN_AMPLITUDE }
                .forEach { pair -> noteToDelete.addFirst(pair) }
        track.getTrack().removeAll(noteToDelete)
    }

    private fun joinSounds(track: Track) {
        val trackArray = track.getTrackArray()
        val joinedTruck = LinkedList<Pair<Int, Note>>()

        val maxJoining = (1.0 / (track.minNote.length / NoteLength.FULL.length)).toInt()
        val joiningOptions = prepareJoinOptions(maxJoining)

        var currentIndex = 0
        while (currentIndex < trackArray.size) {
            var joined = false
            for (option in joiningOptions) {
                if (trackArray.size - currentIndex < option) {
                    continue
                }
                if (canJoin(trackArray, option, currentIndex)) {
                    joinedTruck.addLast(joinNotes(trackArray, option, currentIndex))
                    currentIndex += option
                    joined = true
                    break
                } else {
                    continue
                }
            }
            if (!joined) {
                joinedTruck.addLast(trackArray[currentIndex])
                currentIndex++
            }
        }
        track.setListOfSound(joinedTruck)
    }

    private fun prepareJoinOptions(maxJoining: Int): Array<Int> {
        var joining = maxJoining
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
            checkLengthsAreDifference(array, toJoin, startPoint) -> false
            checkAmplitudesAreNotMonotonic(array, toJoin, startPoint) -> false
            else -> true
        }
    }

    private fun checkIndexesAreNotOrdered(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        return (1 until toJoin).any { i -> array[startPoint + i - 1].first + 1 != array[startPoint + i].first }
    }

    private fun checkNamesAreDifference(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        val referenceName = array[startPoint].second.name
        return (1 until toJoin).any { i -> array[startPoint + i].second.name != referenceName }
    }

    private fun checkLengthsAreDifference(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        val referenceLength = array[startPoint].second.length
        return (1 until toJoin).any { i -> array[startPoint + i].second.length != referenceLength }
    }

    private fun checkAmplitudesAreNotMonotonic(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Boolean {
        return (1 until toJoin).any { i ->
            array[startPoint + i - 1].second.amplitude < array[startPoint + i].second.amplitude &&
                    abs(array[startPoint + i - 1].second.amplitude - array[startPoint + i].second.amplitude) > array[startPoint + i - 1].second.amplitude * AMPLITUDE_MARGIN
        }
    }

    private fun joinNotes(array: Array<Pair<Int, Note>>, toJoin: Int, startPoint: Int): Pair<Int, Note> {
        val index = array[startPoint].first
        val name = array[startPoint].second.name
        val frequency = array[startPoint].second.frequency
        val staffPosition = array[startPoint].second.staffPosition
        val isHalfTone = array[startPoint].second.isHalfTone

        val note = Note(name, frequency, staffPosition, isHalfTone)
        note.amplitude = (0 until toJoin).sumByDouble { i -> array[startPoint + i].second.amplitude }
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