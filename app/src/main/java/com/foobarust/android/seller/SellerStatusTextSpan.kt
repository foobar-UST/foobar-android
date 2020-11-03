package com.foobarust.android.seller

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.foobarust.android.utils.CenteredImageSpan
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.getDrawableOrNull
import com.foobarust.android.utils.setTintCompat
import kotlin.math.round

/**
 * Created by kevin on 10/17/20
 */

data class SellerStatusTextSpan(
    val spanText: String,
    @DrawableRes val drawableRes: Int,
    @ColorRes val drawableTint: Int,
    val drawableScale: Double = 1.0
)

fun SellerStatusTextSpan.addToStatusTextView(statusTextView: TextView) {
    val context = statusTextView.context
    val spannableBuilder = SpannableStringBuilder(statusTextView.text)
    val spannable = SpannableString("  ${this.spanText}")

    val drawable = context.getDrawableOrNull(this.drawableRes)!!

    // Set drawable size
    val drawableSize = round(statusTextView.lineHeight * this.drawableScale).toInt()
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

    statusTextView.text = spannableBuilder
}

