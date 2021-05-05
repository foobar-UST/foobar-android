package com.foobarust.android.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.foobarust.android.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.ktx.addMarker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by kevin on 4/6/21
 */

fun GoogleMap.addMarker(
    context: Context,
    latLng: LatLng,
    @DrawableRes drawableRes: Int,
    hasBorder: Boolean = false
): Marker {
    val drawable = context.getDrawableOrNull(drawableRes) ?:
        throw IllegalStateException("Drawable not found.")

    val pixels = computeMarkerPixels(context.resources.displayMetrics.density)
    var bitmap = Bitmap.createScaledBitmap(drawable.toBitmap(), pixels, pixels, true)

    if (hasBorder) {
        bitmap = bitmap.createBitmapWithBorder(
            borderSize = 10f,
            borderColor = context.getColorCompat(R.color.white)
        )
    }

    return addMarker {
        position(latLng)
    }.apply {
        setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
    }
}

suspend fun GoogleMap.loadMarker(
    context: Context,
    latLng: LatLng,
    imageUrl: String?,
    @DrawableRes placeholder: Int,
    hasBorder: Boolean = false
): Marker = suspendCancellableCoroutine { continuation ->
    val request = Glide.with(context)
        .asBitmap()
        .load(imageUrl)
        .circleCrop()
        .placeholder(context.getDrawableOrNull(placeholder))

    val customTarget = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            val pixels = computeMarkerPixels(context.resources.displayMetrics.density)
            var bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true)

            if (hasBorder) {
                bitmap = bitmap.createBitmapWithBorder(
                    borderSize = 10f,
                    borderColor = context.getColorCompat(R.color.white)
                )
            }

            addMarker {
                position(latLng)
            }.apply {
                setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                continuation.resume(this)
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) = Unit
    }

    request.into(customTarget)

    continuation.invokeOnCancellation {
        Glide.with(context).clear(customTarget)
    }
}

private fun computeMarkerPixels(density: Float): Int {
    return (50f * density + 0.5f).toInt()
}

private fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = halfWidth.coerceAtMost(halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
        width + borderOffset,
        height + borderOffset,
        Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    return newBitmap
}