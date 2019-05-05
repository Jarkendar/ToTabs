package com.jarkendar.totabs.analyzer

import android.util.Log
import com.jarkendar.totabs.analyzer.note_parser.NoteLength
import com.jarkendar.totabs.analyzer.note_parser.NoteMatcher
import com.jarkendar.totabs.analyzer.wavfiles.WavFile
import com.jarkendar.totabs.analyzer.wavfiles.WavFileException
import org.jtransforms.fft.DoubleFFT_1D
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Analyzer constructor(private val musicFileHolder: MusicFileHolder) {

    public var beatsPerMinute: Int = 60

    private val TAG = "*******"
    private val noteMatcher = NoteMatcher()
    private lateinit var track: Track
    private var doubleFFT_1D: Pair<Int, DoubleFFT_1D>? = null

    //todo divide counting to threads

    public fun analyze() {
        val noteAndPart = calcPairNoteLengthAndPartOfSecond(musicFileHolder.getSampleRate(), beatsPerMinute)
        track = Track(beatsPerMinute, noteAndPart.first, noteAndPart.second)
        val mimeString = musicFileHolder.getMIMEType()
        when {
            mimeString.contains("wav") -> wavAnalyze(track.minNoteDuration)
        }

        Log.d(TAG, track.toString())
        TrackCompressor().compressTrack(track)
        Log.d(TAG, track.toString())
    }

    private fun calcPairNoteLengthAndPartOfSecond(sampleRate: Int, beatsPerMinute: Int): Pair<NoteLength, Double> {
        var noteInSecond = beatsPerMinute / SECONDS_IN_MINUTE * NoteLength.FULL.length//start from full notes in second
        var frequencyRecognizing = sampleRate / 2.0

        while (frequencyRecognizing > THRESHOLD_OF_FREQUENCY && noteInSecond > THRESHOLD_NOTE) {
            frequencyRecognizing /= 2.0
            noteInSecond /= 2.0
        }
        val nearestNoteLength = calcNearestGreaterNoteLength(noteInSecond)
        val partOfSecond = (nearestNoteLength.length / NoteLength.FULL.length) * (SECONDS_IN_MINUTE / beatsPerMinute)
        Log.d(TAG, "$nearestNoteLength, $partOfSecond")

        return Pair(nearestNoteLength, partOfSecond)
    }

    private fun calcNearestGreaterNoteLength(noteValue: Double): NoteLength {
        var lengthValue = NoteLength.FULL.length
        while (lengthValue / 2.0 >= noteValue) {
            lengthValue /= 2.0
        }
        NoteLength.values().forEach { noteLength -> if (noteLength.length == lengthValue) return noteLength }
        return NoteLength.FULL
    }

    private fun wavAnalyze(partDuration: Double) {
        try {
            val wavFile = WavFile.openWavFile(musicFileHolder.musicFile)
            wavFile.display()

            Log.d(TAG, "$wavFile.numChannels, $wavFile.numFrames, $wavFile.sampleRate, ${wavFile.numFrames / wavFile.sampleRate}")
            val bufferSize = (partDuration * wavFile.sampleRate).toInt()
            Log.d(TAG, "$bufferSize")
            val buffer = DoubleArray(bufferSize * wavFile.numChannels)

            while (wavFile.readFrames(buffer, bufferSize) != 0) {
                fft(buffer, wavFile.sampleRate)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }
    }

    private fun fft(doubleArray: DoubleArray, sampleRate: Long) {
        if (doubleFFT_1D == null || doubleFFT_1D!!.first != doubleArray.size) {
            doubleFFT_1D = Pair(doubleArray.size, DoubleFFT_1D(doubleArray.size.toLong()))
        }
        val fftData = DoubleArray(doubleArray.size * 2)
        for (i in 0 until doubleArray.size) {
            fftData[i * 2] = doubleArray[i]
            fftData[i * 2 + 1] = 0.0
        }
        doubleFFT_1D!!.second.realForward(fftData)
        val bestPairsFrequencyAmplitude = getBestFrequenciesWithAmplitude(fftData, sampleRate)
        track.appendSound(noteMatcher.match(bestPairsFrequencyAmplitude))
    }

    /**
     * Return Array of Pair(frequency, weighted sum harmonic amplitudes)
     */
    private fun getBestFrequenciesWithAmplitude(fftArray: DoubleArray, sampleRate: Long): Array<Pair<Double, Double>> {
        val frequenciesWithAmplitudes = calculateFrequenciesWithAmplitudes(fftArray, sampleRate)
        val harmonicSum = calculateWeightedSumHarmonic(frequenciesWithAmplitudes)
        sortArrayOfPair(harmonicSum)
        val resultPairs = cutBestFirst(harmonicSum)
        val enhancePairs = enhanceCloseTones(resultPairs)
        sortArrayOfPair(enhancePairs)
        Log.d(TAG, Arrays.toString(resultPairs))
        Log.d(TAG, Arrays.toString(enhancePairs))
        return enhancePairs
    }

    private fun calculateFrequenciesWithAmplitudes(fftArray: DoubleArray, sampleRate: Long): Array<Pair<Double, Double>> {//Pair(frequency, amplitude)
        val frequenciesWithAmplitudes = ArrayList<Pair<Double, Double>>(fftArray.size / 2)
        val frequencyMultiplier = 8.0 * (sampleRate.toDouble() / fftArray.size)
        for (i in 0 until fftArray.size / 2) {
            val frequency = i * frequencyMultiplier
            if (frequency in LOWER_BOUND_HUMAN_PERCEPT_HZ..UPPER_BOUND_HUMAN_PERCEPT_HZ && frequency >= LOWER_GUITAR_THRESHOLD) {
                val real = fftArray[2 * i]
                val imaginary = fftArray[2 * i + 1]
                frequenciesWithAmplitudes.add(Pair(frequency, Math.sqrt(real * real + imaginary * imaginary)))
            }
        }
        return frequenciesWithAmplitudes.toTypedArray()
    }

    private fun calculateWeightedSumHarmonic(frequenciesWithAmplitudes: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
        val result = ArrayList<Pair<Double, Double>>(frequenciesWithAmplitudes.size)
        for (i in 0 until frequenciesWithAmplitudes.size) {
            var sum = 0.0
            (1..HARMONIC_TO_ANALYZE)
                    .filter { harmonicNumber -> i * harmonicNumber < frequenciesWithAmplitudes.size }
                    .forEach { harmonicNumber -> sum += (frequenciesWithAmplitudes[i * harmonicNumber].second / harmonicNumber) }
            result.add(i, Pair(frequenciesWithAmplitudes[i].first, sum))
        }
        return result.toTypedArray()
    }

    /**
     * Sorted descending by second param in pair
     */
    private fun sortArrayOfPair(array: Array<Pair<Double, Double>>) {
        array.sortWith(kotlin.Comparator { o1, o2 ->
            val compareValue = -compareValues(o1.second, o2.second)
            return@Comparator if (compareValue != 0) {
                compareValue
            } else {
                compareValues(o1.first, o2.first)
            }
        })
    }

    private fun cutBestFirst(sortedArray: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
        return sortedArray.toList().subList(0, BEST_FIRST).toTypedArray()
    }

    private fun enhanceCloseTones(array: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
        val values = DoubleArray(array.size)
        val result = ArrayList<Pair<Double, Double>>(BEST_FIRST)
        for (i in 0 until array.size) {
            values[i] += array[i].second
            for (j in 0 until array.size) {
                if (i != j && isDifferenceSignificant(array[i].first, array[j].first)) {
                    values[j] += array[i].second * ENHANCE_POWER
                }
            }
        }
        (0 until array.size).forEach { result.add(Pair(array[it].first, values[it])) }
        return result.toTypedArray()
    }

    private fun isDifferenceSignificant(value1: Double, value2: Double): Boolean {
        return if (value1 > value2) {
            value1 < value2 * DIFFERENCE_TONE && value1 > value2 / DIFFERENCE_TONE
        } else {
            value2 < value1 * DIFFERENCE_TONE && value2 > value1 / DIFFERENCE_TONE
        }
    }

    companion object {
        private const val HARMONIC_TO_ANALYZE = 6
        private const val BEST_FIRST = 6
        private const val UPPER_BOUND_HUMAN_PERCEPT_HZ = 20_000.0
        private const val LOWER_BOUND_HUMAN_PERCEPT_HZ = 20.0
        private const val LOWER_GUITAR_THRESHOLD = 75.0
        private val DIFFERENCE_TONE = Math.pow(2.0, 1.0 / 7.0)
        private const val ENHANCE_POWER = 0.5
        private const val SECONDS_IN_MINUTE = 60.0
        private const val THRESHOLD_OF_FREQUENCY = 1400.0
        private val THRESHOLD_NOTE = NoteLength.EIGHTH.length
    }
}