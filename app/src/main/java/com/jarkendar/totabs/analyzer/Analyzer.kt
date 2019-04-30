package com.jarkendar.totabs.analyzer

import android.util.Log
import com.jarkendar.totabs.analyzer.wavfiles.WavFile
import com.jarkendar.totabs.analyzer.wavfiles.WavFileException
import org.jtransforms.fft.DoubleFFT_1D
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class Analyzer constructor(private val musicFileHolder: MusicFileHolder) {

    private val TAG = "*******"

    public fun analyze() {
        val mimeString = musicFileHolder.getMIMEType()
        if (mimeString.contains("wav")) {
            wavAnalyze()
        }
    }

    private fun wavAnalyze() {
        try {
            val wavfile = WavFile.openWavFile(musicFileHolder.musicFile)
            wavfile.display()

            val partOfSecond = 0.3

            val channel = wavfile.numChannels
            val frames = wavfile.numFrames
            val samleRate = wavfile.sampleRate

            Log.d(TAG, "$channel, $frames, $samleRate, ${frames / samleRate}")
            val bufferSize = (partOfSecond * samleRate).toInt()
            Log.d(TAG, "$bufferSize")
            val buffer = DoubleArray(bufferSize * channel)
            var readFrames: Int

            do {
                readFrames = wavfile.readFrames(buffer, bufferSize)
                fft(buffer, samleRate)
            } while (readFrames != 0)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }
    }

    private fun fft(doubleArray: DoubleArray, sampleRate: Long) {
        val doubleFFT_1D = DoubleFFT_1D(doubleArray.size.toLong())
        val fftData = DoubleArray(doubleArray.size * 2)
        for (i in 0 until doubleArray.size) {
            fftData[2 * i] = doubleArray[i]
            fftData[2 * i + 1] = 0.0
        }
        doubleFFT_1D.realForward(fftData)
        val maxFrequency = getMaxFrequency(fftData, sampleRate)
        getMaxFrequenciesBaseOnHarmonic(fftData, sampleRate)
        Log.d("****d****", maxFrequency.toString())
    }

    /**
     * Return Array of Pair(frequency, weighted sum harmonic amplitudes)
     */
    private fun getMaxFrequenciesBaseOnHarmonic(fftArray: DoubleArray, sampleRate: Long): Array<Pair<Double, Double>> {
        val amplitudes = countAmplitudes(fftArray, sampleRate)
        val harmonicSum = countWeightedSumHarmonic(amplitudes)
        sortArrayOfPair(harmonicSum)
        val resultPairs = cutBestFirst(harmonicSum)
        Log.d(TAG, Arrays.toString(resultPairs))
        return resultPairs
    }

    private fun countAmplitudes(fftArray: DoubleArray, sampleRate: Long): Array<Pair<Double, Double>> {//Pair(frequency, amplitude)
        val amplitudes = ArrayList<Pair<Double, Double>>(fftArray.size / 2)
        for (i in 0 until fftArray.size / 2) {
            val re = fftArray[2 * i]
            val im = fftArray[2 * i + 1]
            val freq = i * 8.0 * (sampleRate.toDouble() / fftArray.size)
            if (freq in LOWER_BOUND_HUMAN_PERCEPT_HZ..UPPER_BOUND_HUMAN_PERCEPT_HZ) {
                amplitudes.add(Pair(freq, Math.sqrt(re * re + im * im)))
            }
        }
        return amplitudes.toTypedArray()
    }

    private fun countWeightedSumHarmonic(amplitudes: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
        val result = ArrayList<Pair<Double, Double>>(amplitudes.size)
        for (i in 0 until amplitudes.size) {
            var sum = 0.0
            for (h in 1..HARMONIC_TO_ANALYZE) {
                if (i * h < amplitudes.size) {
                    sum += amplitudes[i * h].second / h
                }
            }
            result.add(i, Pair(amplitudes[i].first, sum))
        }
        return result.toTypedArray()
    }

    /**
     * Sorted descending by second param in pair
     */
    private fun sortArrayOfPair(array: Array<Pair<Double, Double>>) {
        array.sortWith(kotlin.Comparator { o1, o2 -> -compareValues(o1.second, o2.second) })
    }

    private fun cutBestFirst(array: Array<Pair<Double, Double>>): Array<Pair<Double, Double>> {
        val result = ArrayList<Pair<Double, Double>>(BEST_FIRST)
        for (i in 0 until if (BEST_FIRST <= array.size) BEST_FIRST else array.size) {
            result.add(i, array[i])
        }
        return result.toTypedArray()
    }

    private fun getMaxFrequency(fftArray: DoubleArray, sampleRate: Long): Double {
        var maxMagnitude = 1.0
        var maxIndex = 0
        for (i in 0 until fftArray.size / 2) {
            val re = fftArray[2 * i]
            val im = fftArray[2 * i + 1]
            val magnitude = Math.sqrt(re * re + im * im)
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude
                maxIndex = i
            }
        }
        Log.d(TAG, "$maxIndex, $maxMagnitude, ${fftArray.size}")
        return maxIndex * 8.0 * (sampleRate.toDouble() / fftArray.size)
    }

    private fun printArray(doubleArray: DoubleArray) {
        Log.d("****doubleArray****", Arrays.toString(doubleArray))
    }

    companion object {
        private val HARMONIC_TO_ANALYZE = 4
        private val BEST_FIRST = 6
        private val UPPER_BOUND_HUMAN_PERCEPT_HZ = 20_000.0
        private val LOWER_BOUND_HUMAN_PERCEPT_HZ = 20.0
    }
}