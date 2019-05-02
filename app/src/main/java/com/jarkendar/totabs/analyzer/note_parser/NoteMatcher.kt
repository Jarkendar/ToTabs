package com.jarkendar.totabs.analyzer.note_parser

class NoteMatcher {

    private var noteSpectreGenerator: NoteSpectreGenerator = NoteSpectreGenerator()

    public fun match(recognizeFrequencies: Array<Pair<Double, Double>>): Note {
        //todo match to chords, generalize note to sound

        val highestAmpSound = recognizeFrequencies[0]
        return chooseLowestSEFromNotes(highestAmpSound)
    }

    private fun chooseLowestSEFromNotes(reference: Pair<Double, Double>): Note {
        val notes = noteSpectreGenerator.getNotes()

        var minSE = Double.MAX_VALUE
        var minSEIndex = -1
        for (i in 0 until notes.size) {
            val squareError = Math.pow((reference.first - notes[i].frequency), 2.0)
            if (minSE > squareError) {
                minSE = squareError
                minSEIndex = i
            }
        }
        return notes[minSEIndex]
    }
}