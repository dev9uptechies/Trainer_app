package com.example.Adapter.training_plan.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.rtugeek.android.colorseekbar.BaseSeekBar
import com.rtugeek.android.colorseekbar.thumb.ThumbDrawer

class CustomThumbDrawer(
    private val size: Int,
    private val borderColor: Int
) : ThumbDrawer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE // Always white background
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    override fun onDrawThumb(thumbBounds: RectF?, seekBar: BaseSeekBar?, canvas: Canvas?) {
        if (canvas == null || thumbBounds == null) return // Avoid crashes

        val cx = thumbBounds.centerX()
        val cy = thumbBounds.centerY()
        val radius = size / 2f

        canvas.drawCircle(cx, cy, radius, paint) // White background
        canvas.drawCircle(cx, cy, radius, borderPaint) // Red border
    }

    override fun getWidth(): Int = size

    override fun getHeight(): Int = size


}
