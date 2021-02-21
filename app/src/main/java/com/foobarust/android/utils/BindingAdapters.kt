package com.foobarust.android.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.DrawableRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator

/**
 * Expand the [AppBarLayout].
 * @param expand the expand condition.
 */
@BindingAdapter("expandIf")
fun AppBarLayout.bindExpandIf(expand: Boolean) {
    setExpanded(expand)
}

/**
 * Start view animation when the view is loaded.
 * @param animRes the resource id of the animation.
 */
@BindingAdapter("startAnim")
fun View.bindStartAnimation(@AnimRes animRes: Int?) {
    if (animRes == null) return
    val animation = AnimationUtils.loadAnimation(context, animRes)
    startAnimation(animation)
}

@BindingAdapter("goneAfterHide")
fun LinearProgressIndicator.bindGoneAfterHide(gone: Boolean) {
    if (gone) setVisibilityAfterHide(GONE)
}

/**
 * Hide progress indicator in a given condition.
 * @param hide the hide condition.
 */
@BindingAdapter("progressHideIf")
fun LinearProgressIndicator.bindProgressHideIf(hide: Boolean) {
    if (hide) hide() else show()
}

/**
 * Set the [MaterialButton] icon from a given [Drawable] resource.
 * @param iconRes the resource id of the drawable.
 */
@BindingAdapter("iconRes")
fun MaterialButton.bindIconRes(@DrawableRes iconRes: Int?) {
    if (iconRes == null) return
    setIconResource(iconRes)
}

/**
 * Control the visibility of the [FloatingActionButton].
 * @param show the condition for showing the button.
 */
@BindingAdapter("showIf")
fun FloatingActionButton.showIf(show: Boolean) {
    if (show) show() else hide()
}

/**
 * Request focus for a specific view.
 * @param focus the condition for requesting focus.
 */
@BindingAdapter("requestFocus")
fun View.bindRequestFocus(focus: Boolean) {
    if (focus) requestFocus()
}

@BindingAdapter("popupElevationOverlay")
fun Spinner.bindPopupElevationOverlay(popupElevationOverlay: Float) {
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
@BindingAdapter(
    "drawableLeft",
    "drawableTop",
    "drawableRight",
    "drawableBottom",
    requireAll = false
)
fun TextView.bindDrawables(
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

@BindingAdapter(
    "drawableFitVertical"
)
fun TextView.bindDrawableFitVertical(fitVertical: Boolean) {
    if (!fitVertical) return

    val drawableSize = lineHeight
    val updatedDrawables = compoundDrawablesRelative.map { drawable: Drawable? ->
        drawable?.setBounds(0, 0, drawableSize, drawableSize)
        drawable
    }

    setCompoundDrawables(
        updatedDrawables[0],        /* left */
        updatedDrawables[1],        /* top */
        updatedDrawables[2],        /* right */
        updatedDrawables[3]         /* bottom */
    )
}

/*
/**
 * Set a Chip's leading icon using Glide.
 * Optionally set the image to be center cropped and/or cropped to a circle.
 */
@BindingAdapter(
    "glideChipIcon",
    "glideChipIconCenterCrop",
    "glideChipIconCircularCrop",
    requireAll = false
)
fun Chip.bindGlideChipSrc(
    @DrawableRes drawableRes: Int?,
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
        resources.getDimensionPixelSize(R.dimen.chip_icon_diameter),
        resources.getDimensionPixelSize(R.dimen.chip_icon_diameter)
    )
}

 */

/**
 * Set the [ImageView] using a [Drawable] resource.
 * @param drawableRes the resource id of the drawable.
 */
@BindingAdapter(
    "src"
)
fun ImageView.bindSrc(
    @DrawableRes drawableRes: Int?
) {
    if (drawableRes == null) return
    val drawable = context.getDrawableOrNull(drawableRes)
    drawable?.let { setImageDrawable(it) }
}

/**
 * Set the [ImageView] by loading an image using a given url.
 * @param imageUrl the url of the image.
 * @param centerCrop whether to apply center cropping the image.
 * @param circularCrop whether to apply circular cropping to the image.
 * @param placeholder the resource id of the fallback drawable if there is
 * network error.
 */
@BindingAdapter(
    "glideUrl",
    "glideCenterCrop",
    "glideCircularCrop",
    "glidePlaceholder",
    requireAll = false
)
fun ImageView.bindGlideUrl(
    imageUrl: String?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false,
    @DrawableRes placeholder: Int? = null
) {
    // Use local drawable if the given url is null.
    if (imageUrl == null) {
        bindGlideSrc(placeholder, centerCrop, circularCrop)
        return
    }

    createGlideRequest(
        context,
        imageUrl,
        centerCrop,
        circularCrop,
        placeholder
    ).into(this)
}

/**
 * Set the [ImageView] by loading an image using a [Drawable] resource.
 * @param drawableRes the resource id of the drawable.
 * @param centerCrop whether to apply center cropping the image.
 * @param circularCrop whether to apply circular cropping to the image.
 */
@BindingAdapter(
    "glideSrc",
    "glideCenterCrop",
    "glideCircularCrop",
    requireAll = false
)
fun ImageView.bindGlideSrc(
    @DrawableRes drawableRes: Int?,
    centerCrop: Boolean = false,
    circularCrop: Boolean = false
) {
    if (drawableRes == null) return

    createGlideRequest(
        context,
        drawableRes,
        centerCrop,
        circularCrop
    ).into(this)
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

private fun createGlideRequest(
    context: Context,
    imageUrl: String,
    centerCrop: Boolean,
    circularCrop: Boolean,
    placeholder: Int?
): RequestBuilder<Drawable> {
    val req = Glide.with(context).load(imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())

    if (placeholder != null) req.placeholder(context.getDrawableOrNull(placeholder))
    if (centerCrop) req.centerCrop()
    if (circularCrop) req.circleCrop()

    return req
}

@BindingAdapter("goneIf")
fun View.bindGoneIf(gone: Boolean) {
    visibility = if (gone) GONE else VISIBLE
}

@BindingAdapter("hideIf")
fun View.bindHideIf(hide: Boolean) {
    visibility = if (hide) INVISIBLE else VISIBLE
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
    if (previousFullscreen != fullscreen && fullscreen) {
        systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                /* SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION transparent navigation bar */
    }
}

@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

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

@BindingAdapter(
    "marginLeftSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginRightSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsMargin(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

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