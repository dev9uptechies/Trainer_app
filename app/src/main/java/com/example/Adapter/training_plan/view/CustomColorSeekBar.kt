package com.example.Adapter.training_plan.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.SeekBar
import com.rtugeek.android.colorseekbar.ColorSeekBar

class CustomColorSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ColorSeekBar(context, attrs, defStyleAttr) {

    fun setCustomThumb(drawable: Drawable?) {
        try {
            val field = SeekBar::class.java.getDeclaredField("mThumb")
            field.isAccessible = true
            field.set(this, drawable)
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
