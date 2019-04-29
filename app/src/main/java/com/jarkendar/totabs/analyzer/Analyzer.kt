package com.jarkendar.totabs.analyzer

import android.util.Log
import com.jarkendar.totabs.analyzer.wavfiles.WavFile
import com.jarkendar.totabs.analyzer.wavfiles.WavFileException
import org.jtransforms.fft.DoubleFFT_1D
import java.io.IOException
import java.util.*


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
                fft(buffer, samleRate, partOfSecond)
            } while (readFrames != 0)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }
    }

    private fun fft(doubleArray: DoubleArray, sampleRate: Long, partOfSecond: Double) {
        val doubleFFT_1D = DoubleFFT_1D(doubleArray.size.toLong())
        val fftData = DoubleArray(doubleArray.size * 2)
        for (i in 0 until doubleArray.size) {
            fftData[2 * i] = doubleArray[i]
            fftData[2 * i + 1] = 0.0
        }
        doubleFFT_1D.realForward(fftData)
        val maxFrequency = getMaxFrequency(fftData, sampleRate, partOfSecond)
        Log.d("****d****", maxFrequency.toString())
    }


    private fun getMaxFrequency(fftArray: DoubleArray, sampleRate: Long, partOfSecond: Double): Double {
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
}