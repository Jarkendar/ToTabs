package com.jarkendar.totabs.draftsmen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.jarkendar.totabs.analyzer.Track

class StaffDraftsman {

    private var bitmapHeight: Int = 0
    private var bitmapWidth: Int = 0

    public fun drawTrack(bitmap: Bitmap, track: Track, radius: Float) {
        initSize(bitmap)
        val defaultPaint = prepareDefaultPaint()
        val canvas = Canvas(bitmap)

        prepareStaff(canvas, defaultPaint, radius)

    }

    private fun initSize(bitmap: Bitmap) {
        bitmapHeight = bitmap.height
        bitmapWidth = bitmap.width
    }

    private fun prepareStaff(canvas: Canvas, paint: Paint, radius: Float) {
        drawPrefixBoundLine(canvas, paint, radius)
        for (i in 0 until 5) {
            drawStaffLine(canvas, paint, radius, i)
        }
        drawSuffixBoundLine(canvas, paint, radius)
    }

    private fun drawPrefixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(PREFIX * radius, (ABOVE_STAFF + MARGIN) * 2 * radius, PREFIX * radius, (STANDARD_STAFF + ABOVE_STAFF + MARGIN) * 2 * radius, paint)
    }

    private fun drawSuffixBoundLine(canvas: Canvas, paint: Paint, radius: Float) {
        canvas.drawLine(bitmapWidth - SUFFIX * radius, (ABOVE_STAFF + MARGIN) * 2 * radius, bitmapWidth - SUFFIX * radius, (STANDARD_STAFF + ABOVE_STAFF + MARGIN) * 2 * radius, paint)
    }

    private fun drawStaffLine(canvas: Canvas, paint: Paint, radius: Float, numberOfLine: Int) {
        canvas.drawLine(PREFIX * radius, (ABOVE_STAFF + MARGIN + numberOfLine) * 2 * radius, bitmapWidth - SUFFIX * radius, (ABOVE_STAFF + MARGIN + numberOfLine) * 2 * radius, paint)

    }

    private fun prepareDefaultPaint(): Paint {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.0f
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
    }
}