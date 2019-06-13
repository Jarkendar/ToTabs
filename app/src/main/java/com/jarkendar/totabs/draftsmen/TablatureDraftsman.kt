package com.jarkendar.totabs.draftsmen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.Log
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.analyzer.note_parser.Note
import com.jarkendar.totabs.generators.NeckNoteGenerator
import java.util.*

class TablatureDraftsman constructor(val context: Context, val tuning: Array<String>, val maxFret: Int) {

    private var bitmapHeight: Int = 0
    private var bitmapWidth: Int = 0
    private var firstLineY: Int = 0

    public fun drawTrack(bitmap: Bitmap, track: Track, radius: Float) {
        initSize(bitmap)
        countFirstLinePosition(radius)

        val defaultPaint = prepareDefaultPaint()
        val textPaint = prepareTextPaint(defaultPaint)
        val canvas = Canvas(bitmap)

        writeTuningOnCanvas(canvas, textPaint, tuning, radius)
        prepareTablature(canvas, defaultPaint, radius, tuning)

        val neckNoteGenerator = NeckNoteGenerator(tuning, maxFret)
        Log.d("**********", neckNoteGenerator.toString())

        drawNotes(canvas, track.getTrackArray(), radius, textPaint, neckNoteGenerator.neckNote, prepareRectanglePaint(defaultPaint))
    }

    private fun writeTuningOnCanvas(canvas: Canvas, textPaint: TextPaint, tuning: Array<String>, radius: Float) {
        (0 until tuning.size).forEach { i -> canvas.drawText(tuning[i], PREFIX / 2.5f * radius, firstLineY + 2 * radius * i + radius, textPaint) }
    }

    private fun prepareTablature(canvas: Canvas, defaultPaint: Paint, radius: Float, tuning: Array<String>) {
        drawPrefixBoundLine(canvas, defaultPaint, radius)
        (0 until tuning.size).forEach { drawTablatureLine(canvas, defaultPaint, radius, it) }
        drawSuffixBoundLine(canvas, defaultPaint, radius)
    }

    private fun drawPrefixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(
                PREFIX * radius,
                firstLineY.toFloat(),
                PREFIX * radius,
                firstLineY.toFloat() + 10 * radius,
                paint)
    }

    private fun drawTablatureLine(canvas: Canvas, paint: Paint, radius: Float, numberOfLine: Int) {
        canvas.drawLine(
                PREFIX * radius,
                firstLineY.toFloat() + numberOfLine * 2 * radius,
                bitmapWidth - SUFFIX * radius,
                firstLineY.toFloat() + numberOfLine * 2 * radius,
                paint)
    }

    private fun drawSuffixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(
                bitmapWidth - SUFFIX * radius,
                firstLineY.toFloat(),
                bitmapWidth - SUFFIX * radius,
                firstLineY.toFloat() + 10 * radius,
                paint)
    }

    private fun drawNotes(canvas: Canvas, track: Array<Pair<Int, Note>>, radius: Float, textPaint: TextPaint, neckNote: Array<Array<Pair<String, Int>>>, rectanglePaint: Paint) {
        (0 until track.size).forEach { i ->
            val position = findNoteOnNeck(track[i].second, neckNote)
            Log.d("********", position.toString() + " " + track[i].second.toString())
            var addition = -radius * 2 / 3
            if (position.second >= 10) {
                addition = radius
            }
            canvas.drawRect(
                    FIRST_NOTE_POSITION * radius + ONE_NOTE_AREA * radius * i
                    , firstLineY + 2 * radius * position.first - radius
                    , FIRST_NOTE_POSITION * radius + ONE_NOTE_AREA * radius * i + 2 * radius + addition
                    , firstLineY + 2 * radius * position.first + radius
                    , rectanglePaint)
            canvas.drawText(
                    position.second.toString()
                    , FIRST_NOTE_POSITION * radius + ONE_NOTE_AREA * radius * i
                    , firstLineY + 2 * radius * position.first + radius
                    , textPaint)
        }
    }

    /**
     * Return Pair<string number, fret number>
     */
    private fun findNoteOnNeck(note: Note, neckNote: Array<Array<Pair<String, Int>>>): Pair<Int, Int> {
        val pairsList = LinkedList<Pair<Int, Int>>()
        (0 until tuning.size).forEach { i ->
            (0 until neckNote[i].size).forEach { j ->
                if (neckNote[i][j].first == note.name) pairsList.addFirst(Pair(i, j))
            }
        }
        return pairsList.minBy { pair -> pair.second } ?: Pair(-1, -1)
    }

    private fun initSize(bitmap: Bitmap) {
        bitmapWidth = bitmap.width
        bitmapHeight = bitmap.height
    }

    private fun countFirstLinePosition(radius: Float) {
        firstLineY = ((bitmapHeight - 10 * radius) / 2).toInt()
    }

    private fun prepareDefaultPaint(): Paint {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.0f
        return paint
    }

    private fun prepareRectanglePaint(defaultPaint: Paint): Paint {
        val paint = Paint(defaultPaint)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#fff3f3f3")
        return paint
    }

    private fun prepareTextPaint(defaultPaint: Paint): TextPaint {
        val textPaint = TextPaint(defaultPaint)
        textPaint.textSize = context.resources.getDimension(R.dimen.text_size)
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 1.0f
        textPaint.isAntiAlias = true
        return textPaint
    }

    companion object {
        public const val PREFIX = 7.5f
        public const val SUFFIX = 7.5f
        public const val ONE_NOTE_AREA = 6.0f
        public const val FIRST_NOTE_POSITION = PREFIX + 9.5f


    }
}