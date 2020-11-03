package com.foobarust.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.annotation.NonNull
import java.lang.ref.WeakReference

/**
 * Created by kevin on 10/17/20
 */

class CenteredImageSpan(drawable: Drawable) : ImageSpan(drawable) {

    private var drawableRef: WeakReference<Drawable>? = null

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val rect = cachedDrawable!!.bounds
        val pfm = paint.fontMetricsInt

        if (fm != null) {
            fm.ascent = -rect.height() / 2 + pfm.ascent / 2
            fm.descent = 0.coerceAtLeast(rect.height() / 2 + pfm.ascent / 2)
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }

        return rect.right
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, @NonNull paint: Paint) {
        canvas.save()

        val transY = (bottom + top) / 2 - cachedDrawable!!.bounds.height() / 2

        canvas.translate(x, transY.toFloat())
        cachedDrawable!!.draw(canvas)
        canvas.restore()
    }

    // Redefined locally because it is a private member from DynamicDrawableSpan
    private val cachedDrawable: Drawable?
        get() {
            val weakReference = drawableRef
            var drawable: Drawable? = null

            if (weakReference != null) drawable = weakReference.get()

            if (drawable == null) {
                drawable = this.drawable
                drawableRef = WeakReference(drawable)
            }

            return drawable
        }
}