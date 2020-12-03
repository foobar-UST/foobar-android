package com.foobarust.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import java.lang.ref.WeakReference
import kotlin.math.round

/**
 * Created by kevin on 10/17/20
 */

data class DrawableTextSpan(
    val spanText: String,
    @DrawableRes val drawableRes: Int,
    @ColorRes val drawableTint: Int,
    val drawableScale: Double = 1.0
)

fun DrawableTextSpan.applyTo(textView: TextView) {
    val context = textView.context
    val spannableBuilder = SpannableStringBuilder(textView.text)
    val spannable = SpannableString("  ${this.spanText}")

    val drawable = context.getDrawableOrNull(this.drawableRes)!!

    // Set drawable size
    val drawableSize = round(textView.lineHeight * this.drawableScale).toInt()
    drawable.setBounds(0, 0, drawableSize, drawableSize)

    // Set drawable tint
    val drawableColor = context.getColorCompat(this.drawableTint)
    drawable.setTintCompat(drawableColor)

    // Set drawable span
    val imageSpan = CenteredImageSpan(drawable)
    spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    // Append new spannable at the end of text view
    if (spannableBuilder.isNotBlank()) {
        spannableBuilder.append("   ")
    }

    spannableBuilder.append(spannable)

    textView.text = spannableBuilder
}

private class CenteredImageSpan(drawable: Drawable) : ImageSpan(drawable) {

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

