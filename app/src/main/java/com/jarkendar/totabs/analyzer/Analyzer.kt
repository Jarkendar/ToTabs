package com.jarkendar.totabs.analyzer

import android.util.Log
import com.jarkendar.totabs.analyzer.wavfiles.WavFile
import com.jarkendar.totabs.analyzer.wavfiles.WavFileException
import java.io.IOException
import java.util.*


class Analyzer constructor(private val musicFileHolder: MusicFileHolder) {

    private var SAMPLERATE = 0
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

            val channel = wavfile.numChannels
            val frames = wavfile.numFrames
            val samleRate = wavfile.sampleRate

            val buffer = DoubleArray(100 * channel)
            var readFrames = 0

            do {
                readFrames = wavfile.readFrames(buffer, 100)
//                printArray(buffer)
            } while (readFrames != 0)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WavFileException) {
            e.printStackTrace()
        }
    }

    private fun printArray(byteArray: ByteArray) {
        Log.d("****byteArray****", Arrays.toString(byteArray))
    }
}