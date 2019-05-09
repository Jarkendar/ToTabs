package com.jarkendar.totabs.draftsmen

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import com.jarkendar.totabs.R
import com.jarkendar.totabs.analyzer.Track
import com.jarkendar.totabs.analyzer.note_parser.Note
import kotlin.math.abs

class StaffDraftsman constructor(val context: Context) {

    private var bitmapHeight: Int = 0
    private var bitmapWidth: Int = 0

    public fun drawTrack(bitmap: Bitmap, track: Track, radius: Float) {
        initSize(bitmap)
        val defaultPaint = prepareDefaultPaint()
        val canvas = Canvas(bitmap)

        writeBPMOnCanvas(canvas, prepareTextPaint(defaultPaint), track.beatsPerMinute, radius)
        prepareStaff(canvas, defaultPaint, radius)

        drawNotes(canvas, track, radius, defaultPaint)
    }

    private fun initSize(bitmap: Bitmap) {
        bitmapHeight = bitmap.height
        bitmapWidth = bitmap.width
    }

    private fun writeBPMOnCanvas(canvas: Canvas, textPaint: TextPaint, beatsPerMinute: Int, radius: Float) {
        canvas.drawText("$beatsPerMinute ${context.resources.getText(R.string.beats_per_minute_shortcut)}", PREFIX / 3 * radius, (ABOVE_STAFF + MARGIN) * 2 * radius - radius, textPaint)
    }

    private fun prepareTextPaint(defaultPaint: Paint): TextPaint {
        val textPaint = TextPaint(defaultPaint)
        textPaint.textSize = context.resources.getDimension(R.dimen.text_size)
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 1.0f
        textPaint.isAntiAlias = true
        return textPaint
    }

    private fun prepareStaff(canvas: Canvas, paint: Paint, radius: Float) {
        drawPrefixBoundLine(canvas, paint, radius)
        (0 until 5).forEach { drawStaffLine(canvas, paint, radius, it) }
        drawSuffixBoundLine(canvas, paint, radius)
    }

    private fun drawPrefixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(
                PREFIX * radius,
                (ABOVE_STAFF + MARGIN) * 2 * radius,
                PREFIX * radius,
                (STANDARD_STAFF + ABOVE_STAFF + MARGIN) * 2 * radius,
                paint)
    }

    private fun drawSuffixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(
                bitmapWidth - SUFFIX * radius,
                (ABOVE_STAFF + MARGIN) * 2 * radius,
                bitmapWidth - SUFFIX * radius,
                (STANDARD_STAFF + ABOVE_STAFF + MARGIN) * 2 * radius,
                paint)
    }

    private fun drawStaffLine(canvas: Canvas, paint: Paint, radius: Float, numberOfLine: Int) {
        canvas.drawLine(
                PREFIX * radius,
                (ABOVE_STAFF + MARGIN + numberOfLine) * 2 * radius,
                bitmapWidth - SUFFIX * radius,
                (ABOVE_STAFF + MARGIN + numberOfLine) * 2 * radius,
                paint)
    }

    private fun drawNotes(canvas: Canvas, track: Track, radius: Float, defaultPaint: Paint) {
        var noteXPosition = FIRST_NOTE_POSITION * radius
        val centerStaff = CENTER_OF_STAFF * 2 * radius

        for (note in track.getTrack()) {
            drawNote(canvas, note, radius, defaultPaint, noteXPosition, centerStaff)
            noteXPosition += ONE_NOTE_AREA * radius
        }
    }

    //todo draw hash before half notes
    private fun drawNote(canvas: Canvas, notePair: Pair<Int, Note>, radius: Float, defaultPaint: Paint, positionX: Float, centerStaff: Float) {
        val noteLength = notePair.second.length
        val noteHeight = notePair.second.staffPosition * 2 * radius

        val dotRectF = RectF(
                positionX - DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                centerStaff + radius - noteHeight,
                positionX + DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                centerStaff - radius - noteHeight)

        val lines = abs(notePair.second.staffPosition).toInt()
        val direction = if (notePair.second.staffPosition >= 0) -1 else 1
        (0..lines).forEach {
            canvas.drawLine(
                    positionX - LINE_AREA_LENGTH / 2 * radius,
                    centerStaff + direction * it * 2 * radius,
                    positionX + LINE_AREA_LENGTH / 2 * radius,
                    centerStaff + direction * it * 2 * radius,
                    defaultPaint)
        }
        if (!noteLength.fill) {
            canvas.drawOval(
                    dotRectF,
                    prepareNotePaint(defaultPaint))
        } else {
            canvas.drawOval(
                    dotRectF,
                    prepareFillPaint(defaultPaint))
        }
        if (noteLength.column) {
            if (notePair.second.staffPosition < 0) {
                canvas.drawLine(
                        positionX + DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                        centerStaff - noteHeight,
                        positionX + DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                        centerStaff - COLUMN_HEIGHT_MULTIPLIER * radius - noteHeight,
                        prepareColumnPaint(defaultPaint))
                (0 until noteLength.numberOfTails).forEach {
                    canvas.drawLine(
                            positionX + DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                            centerStaff - COLUMN_HEIGHT_MULTIPLIER * radius + it * radius - noteHeight,
                            positionX + DOT_WIDTH_RADIUS_MULTIPLIER * radius + TAIL_LENGTH / 2 * radius,
                            centerStaff - COLUMN_HEIGHT_MULTIPLIER * radius + TAIL_LENGTH * radius - it * radius - noteHeight,
                            prepareColumnPaint(defaultPaint))
                }
            } else {
                canvas.drawLine(
                        positionX - DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                        centerStaff - noteHeight,
                        positionX - DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                        centerStaff + COLUMN_HEIGHT_MULTIPLIER * radius - noteHeight,
                        prepareColumnPaint(defaultPaint))
                (0 until noteLength.numberOfTails).forEach {
                    canvas.drawLine(
                            positionX - DOT_WIDTH_RADIUS_MULTIPLIER * radius,
                            centerStaff + COLUMN_HEIGHT_MULTIPLIER * radius - it * radius - noteHeight,
                            positionX - DOT_WIDTH_RADIUS_MULTIPLIER * radius + TAIL_LENGTH / 2 * radius,
                            centerStaff + COLUMN_HEIGHT_MULTIPLIER * radius - TAIL_LENGTH * radius - it * radius - noteHeight,
                            prepareColumnPaint(defaultPaint))
                }
            }

        }
        if (notePair.second.isHalfTone) {
            canvas.drawLine(
                    positionX - 3 * radius,
                    centerStaff - noteHeight - HASH_HEIGHT / 2 * radius,
                    positionX - 3 * radius,
                    centerStaff - noteHeight + HASH_HEIGHT / 2 * radius,
                    prepareColumnPaint(defaultPaint))
            canvas.drawLine(
                    positionX - 2 * radius,
                    centerStaff - noteHeight - HASH_HEIGHT / 2 * radius,
                    positionX - 2 * radius,
                    centerStaff - noteHeight + HASH_HEIGHT / 2 * radius,
                    prepareColumnPaint(defaultPaint))
            canvas.drawLine(
                    positionX - 3.5f * radius,
                    centerStaff - noteHeight + radius,
                    positionX - 1.5f * radius,
                    centerStaff - noteHeight,
                    prepareColumnPaint(defaultPaint))
            canvas.drawLine(
                    positionX - 3.5f * radius,
                    centerStaff - noteHeight,
                    positionX - 1.5f * radius,
                    centerStaff - noteHeight - radius,
                    prepareColumnPaint(defaultPaint))


        }
    }

    private fun prepareDefaultPaint(): Paint {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.0f
        return paint
    }

    private fun prepareNotePaint(defaultPaint: Paint): Paint {
        val paint = Paint(defaultPaint)
        paint.isAntiAlias = true
        paint.strokeWidth = 2.0f
        return paint
    }

    private fun prepareFillPaint(defaultPaint: Paint): Paint {
        val paint = Paint(defaultPaint)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        return paint
    }

    private fun prepareColumnPaint(defaultPaint: Paint): Paint {
        val paint = Paint(defaultPaint)
        paint.strokeWidth = 2.0f
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        return paint
    }

    companion object {
        private const val MARGIN = 1
        private const val BELOW_STAFF = 4
        private const val STANDARD_STAFF = 4
        private const val ABOVE_STAFF = 7
        public const val MAX_LINES_ON_STAFF = MARGIN + BELOW_STAFF + STANDARD_STAFF + ABOVE_STAFF + MARGIN
        public const val PREFIX = 7.5f
        public const val SUFFIX = 7.5f
        public const val ONE_NOTE_AREA = 6.0f
        public const val FIRST_NOTE_POSITION = PREFIX + 9.5f
        private const val CENTER_OF_STAFF = STANDARD_STAFF / 2 + ABOVE_STAFF + MARGIN
        private const val LINE_AREA_LENGTH = 5.0f

        private const val DOT_WIDTH_RADIUS_MULTIPLIER = 1.5f
        private const val COLUMN_HEIGHT_MULTIPLIER = 7.0f
        private const val TAIL_LENGTH = 3.0f
        private const val HASH_HEIGHT = 3.0f
    }
}