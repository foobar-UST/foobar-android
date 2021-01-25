package com.foobarust.android.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes


/**
 * Created by kevin on 1/18/21
 */

data class DrawableTextSpan(
    val displayText: String,
    @DrawableRes val drawableRes: Int? = null,
    @ColorRes val drawableTint: Int? = null,
)

fun List<DrawableTextSpan>.applyTo(textView: TextView, separator: CharSequence = "·") {
    val spansWithSeparators = mutableListOf<CharSequence>()
    forEachIndexed { index, drawableTextSpan ->
        val context = textView.context
        val spannable = SpannableStringBuilder()

        if (drawableTextSpan.drawableRes == null) {
            spannable.append(drawableTextSpan.displayText)
        } else {
            spannable.append("  ${drawableTextSpan.displayText}")
            // Setup icon drawable
            val drawable = context.getDrawableOrNull(drawableTextSpan.drawableRes)!!
            val drawableSize = textView.lineHeight
            val drawableTint = drawableTextSpan.drawableTint

            drawable.setBounds(0, 0, drawableSize, drawableSize)

            if (drawableTint != null) {
                val drawableColor = context.getColorCompat(drawableTint)
                drawable.setTintCompat(drawableColor)
            }

            // Setup image span
            val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM)
            spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Append to span list
        spansWithSeparators.add(spannable)
        if (index != this.lastIndex) {
            spansWithSeparators.add(" $separator ")
        }
    }

    // Fix drawable disappear on api 21 && 22
    textView.text = TextUtils.concat(*spansWithSeparators.map { it }.toTypedArray())
}