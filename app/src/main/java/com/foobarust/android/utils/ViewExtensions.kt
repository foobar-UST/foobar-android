package com.foobarust.android.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.foobarust.android.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar

/**
 * Add multiple [Chip] to a [ChipGroup]
 */
fun ChipGroup.replaceChips(chips: List<Chip>) {
    removeAllViews()
    chips.forEach { addView(it) }
}

/**
 * Prevent dismissing [Snackbar] after calling [Snackbar.setAction]
 */
fun Snackbar.setActionPersist(@StringRes resId: Int, listener: View.OnClickListener): Snackbar {
    return setAction(resId) { /* Do nothing */ }.addCallback(object : Snackbar.Callback() {
        override fun onShown(sb: Snackbar?) {
            sb?.view?.findViewById<View>(R.id.snackbar_action)?.setOnClickListener(listener)
        }
    })
}

/**
 * Hide progress indicator in a given condition.
 * @param hide the hide condition.
 */
fun LinearProgressIndicator.hideIf(hide: Boolean) {
    if (hide) hide() else show()
}

/**
 * Control the visibility of the [FloatingActionButton].
 * @param show the condition for showing the button.
 */
fun FloatingActionButton.showIf(show: Boolean) {
    if (show) show() else hide()
}

fun Spinner.setPopupElevationOverlay(popupElevationOverlay: Float) {
    setPopupBackgroundDrawable(
        ColorDrawable(
            ElevationOverlayProvider(context)
                .compositeOverlayWithThemeSurfaceColorIfNeeded(popupElevationOverlay)
        )
    )
}

/**
 * Set the [Drawable]s in a [TextView] using resource ids.
 * @param drawableLeft the resource id of the left drawable.
 * @param drawableTop the resource id of the top drawable.
 * @param drawableRight the resource id of the right drawable.
 * @param drawableBottom the resource id of the bottom drawable.
 */
fun TextView.setDrawables(
    @DrawableRes drawableLeft: Int? = null,
    @DrawableRes drawableTop: Int? = null,
    @DrawableRes drawableRight: Int? = null,
    @DrawableRes drawableBottom: Int? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(
        context.getDrawableOrNull(drawableLeft),
        context.getDrawableOrNull(drawableTop),
        context.getDrawableOrNull(drawableRight),
        context.getDrawableOrNull(drawableBottom)
    )
}

fun TextView.drawableFitVertical() {
    val drawableSize = lineHeight
    val updatedDrawables = compoundDrawablesRelative.map { drawable: Drawable? ->
        drawable?.setBounds(0, 0, drawableSize, drawableSize)
        drawable
    }

    setCompoundDrawablesRelative(
        updatedDrawables[0],        /* left */
        updatedDrawables[1],        /* top */
        updatedDrawables[2],        /* right */
        updatedDrawables[3]         /* bottom */
    )
}

/**
 * Set the [ImageView] using a [Drawable] resource.
 * @param drawableRes the resource id of the drawable.
 */
fun ImageView.setSrc(
    @DrawableRes drawableRes: Int?
) {
    if (drawableRes == null) return
    val drawable = context.getDrawableOrNull(drawableRes)
    drawable?.let { setImageDrawable(it) }
}

/**
 * Set a Chip's leading icon using Glide.
 * Optionally set the image to be center cropped and/or cropped to a circle.
 */
fun Chip.loadGlideChipSrc(
    @DrawableRes drawableRes: Int?,
    @DimenRes iconDiameter: Int,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false
) {
    if (drawableRes == null) return

    createGlideRequest(
        context,
        drawableRes,
        centerCrop,
        circularCrop
    ).listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean = true

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            chipIcon = resource
            return true
        }
    }).submit(
        resources.getDimensionPixelSize(iconDiameter),
        resources.getDimensionPixelSize(iconDiameter)
    )
}

/**
 * Set the [ImageView] by loading an image using a given url.
 * @param imageUrl the url of the image.
 * @param centerCrop whether to apply center cropping the image.
 * @param circularCrop whether to apply circular cropping to the image.
 * @param placeholder the resource id of the fallback drawable if there is
 * network error.
 */
fun ImageView.loadGlideUrl(
    imageUrl: String?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false,
    @DrawableRes placeholder: Int? = null
) {
    createGlideRequest(
        context,
        imageUrl,
        centerCrop,
        circularCrop,
        placeholder
    ).into(this)
}

private fun createGlideRequest(
    context: Context,
    imageUrl: String?,
    centerCrop: Boolean,
    circularCrop: Boolean,
    placeholder: Int?
): RequestBuilder<Drawable> {
    val req = Glide.with(context)
        .load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())

    if (placeholder != null) req.placeholder(context.getDrawableOrNull(placeholder))
    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()

    return req
}

private fun createGlideRequest(
    context: Context,
    @DrawableRes drawableRes: Int,
    centerCrop: Boolean,
    circularCrop: Boolean
): RequestBuilder<Drawable> {
    val req = Glide.with(context).load(drawableRes)
        .transition(DrawableTransitionOptions.withCrossFade())

    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()

    return req
}

fun View.applyLayoutFullscreen() {
    systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    // SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

fun View.applySystemWindowInsetsPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, padding, _, _ ->
        val systemWindowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsets(WindowInsetsCompat.Type.systemBars())

        val left = if (applyLeft) systemWindowInsets.left else 0
        val top = if (applyTop) systemWindowInsets.top else 0
        val right = if (applyRight) systemWindowInsets.right else 0
        val bottom = if (applyBottom) systemWindowInsets.bottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

fun View.applySystemWindowInsetsMargin(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false
) {
    doOnApplyWindowInsets { view, insets, _, margin, _ ->
        val systemWindowInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
            .getInsets(WindowInsetsCompat.Type.systemBars())

        val left = if (applyLeft) systemWindowInsets.left else 0
        val top = if (applyTop) systemWindowInsets.top else 0
        val right = if (applyRight) systemWindowInsets.right else 0
        val bottom = if (applyBottom) systemWindowInsets.bottom else 0

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margin.left + left
            topMargin = margin.top + top
            rightMargin = margin.right + right
            bottomMargin = margin.bottom + bottom
        }
    }
}

fun View.doOnApplyWindowInsets(
    block: (View, WindowInsets, InitialPadding, InitialMargin, Int) -> Unit
) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    val initialHeight = recordInitialHeightForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin, initialHeight)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        ?: throw IllegalArgumentException("Invalid view layout params")
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

private fun recordInitialHeightForView(view: View): Int {
    return view.layoutParams.height
}