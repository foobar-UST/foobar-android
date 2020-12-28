package com.foobarust.android.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface.BOLD
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.foobarust.android.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.round

interface OnTextViewClickableSpanListener {
    fun onClickableSpanEndClicked(view: View)
}

@BindingAdapter("attachAnim")
fun View.bindAttachAnimation(@AnimRes animRes: Int?) {
    if (animRes == null) return
    val animation = AnimationUtils.loadAnimation(context, animRes)
    startAnimation(animation)
}

@BindingAdapter("progressHideIf")
fun LinearProgressIndicator.bindProgressHideIf(hide: Boolean) {
    if (hide) hide() else show()
}

@BindingAdapter("bottomSheetBackground")
fun ViewGroup.bindBottomSheetBackground(
    bottomSheetBackground: Boolean
) {
    if (!bottomSheetBackground) return

    background = MaterialShapeDrawable(
        context,
        null,
        R.attr.bottomSheetStyle,
        0
    ).apply {
        fillColor = ColorStateList.valueOf(
            context.themeColor(R.attr.colorSurface)
        )
        elevation = resources.getDimension(R.dimen.elevation_xmedium)
        initializeElevationOverlay(context)
    }
}

@BindingAdapter(
    "marginTop",
    "marginBottom",
    "marginStart",
    "marginEnd",
    requireAll = false
)
fun View.bindMargin(
    marginTop: Float?,
    marginBottom: Float?,
    marginStart: Float?,
    marginEnd: Float?
) {
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams

    marginTop?.let { layoutParams.topMargin = it.toInt() }
    marginBottom?.let { layoutParams.bottomMargin = it.toInt() }
    marginStart?.let { layoutParams.marginStart = it.toInt() }
    marginEnd?.let { layoutParams.marginEnd = it.toInt() }

    setLayoutParams(layoutParams)
}

@BindingAdapter(
    "paddingTop",
    "paddingBottom",
    "paddingStart",
    "paddingEnd",
    requireAll = false
)
fun View.bindPadding(
    paddingTop: Float?,
    paddingBottom: Float?,
    paddingStart: Float?,
    paddingEnd: Float?
) {
    paddingTop?.let { updatePadding(top = (getPaddingTop() + it).toInt()) }
    paddingBottom?.let { updatePadding(bottom = (getPaddingBottom() + it).toInt()) }
    paddingStart?.let { updatePadding(left = (paddingLeft + it).toInt()) }
    paddingEnd?.let { updatePadding(right = (paddingRight + it).toInt()) }
}

@BindingAdapter("iconRes")
fun MaterialButton.bindIconRes(@DrawableRes drawableRes: Int?) {
    if (drawableRes == null) return
    setIconResource(drawableRes)
}

@BindingAdapter("showIf")
fun FloatingActionButton.showIf(show: Boolean) {
    if (show) show() else hide()
}

@BindingAdapter("requestFocus")
fun View.bindRequestFocus(requestFocus: Boolean) {
    if (requestFocus) requestFocus()
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

@BindingAdapter(
    "drawableScale"
)
fun TextView.bindDrawableScale(scale: Double?) {
    if (scale == null) return

    val drawableSize = round(lineHeight * scale).toInt()
    val updatedDrawables = compoundDrawablesRelative.map {
        (it as Drawable?)?.setBounds(0, 0, drawableSize, drawableSize)
        it
    }

    setCompoundDrawables(
        updatedDrawables[0],        // left
        updatedDrawables[1],        // top
        updatedDrawables[2],        // right
        updatedDrawables[3]         // bottom
    )
}

@BindingAdapter(
    "drawableLeft",
    "drawableRight",
    "drawableTop",
    "DrawableBottom",
    requireAll = false
)
fun TextView.bindDrawableRes(
    @DrawableRes drawableStartRes: Int?,
    @DrawableRes drawableEndRes: Int?,
    @DrawableRes drawableTopRes: Int?,
    @DrawableRes drawableBottomRes: Int?
) {
    val drawableLeft = drawableStartRes?.let { context.getDrawableOrNull(it) }
    val drawableRight = drawableEndRes?.let { context.getDrawableOrNull(it) }
    val drawableTop = drawableTopRes?.let { context.getDrawableOrNull(it) }
    val drawableBottom = drawableBottomRes?.let { context.getDrawableOrNull(it) }

    setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom)
}

@BindingAdapter("linkMovementMethod")
fun TextView.bindLinkMovementMethod(
    enableLink: Boolean
) {
    if (enableLink) {
        movementMethod = LinkMovementMethod.getInstance()
    }
}

@BindingAdapter(
    "drawableStart",
    "drawableLeft",
    "drawableTop",
    "drawableEnd",
    "drawableRight",
    "drawableBottom",
    requireAll = false
)
fun TextView.bindDrawables(
    @DrawableRes drawableStart: Int? = null,
    @DrawableRes drawableLeft: Int? = null,
    @DrawableRes drawableTop: Int? = null,
    @DrawableRes drawableEnd: Int? = null,
    @DrawableRes drawableRight: Int? = null,
    @DrawableRes drawableBottom: Int? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(
        context.getDrawableOrNull(drawableStart ?: drawableLeft),
        context.getDrawableOrNull(drawableTop),
        context.getDrawableOrNull(drawableEnd ?: drawableRight),
        context.getDrawableOrNull(drawableBottom)
    )
}

@BindingAdapter(
    "colorSpanStart",
    "colorSpanEnd",
    "colorSpanBold",
    "colorSpanColor",
    requireAll = false
)
fun TextView.bindColorSpan(
    spanStart: String? = null,
    spanEnd: String? = null,
    spanBold: Boolean = false,
    @AttrRes spanColor: Int? = null
) {
    if (text.isNullOrBlank()) return

    val color = if (spanColor != null) {
        context.themeColor(spanColor)
    } else {
        context.themeColor(R.attr.colorPrimary)
    }
    val builder = SpannableStringBuilder(text)
    val styles = mutableListOf<Any>(ForegroundColorSpan(color))

    if (spanBold) styles.add(StyleSpan(BOLD))

    spanStart?.let { start ->
        builder.insert(0, start)
        styles.forEach { style ->
            builder.setSpan(
                style,
                0,
                spanStart.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    spanEnd?.let { end ->
        builder.insert(0, end)
        styles.forEach { style ->
            builder.setSpan(
                style,
                builder.length - end.length,
                builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    setText(builder, TextView.BufferType.SPANNABLE)
}

@BindingAdapter(
    "clickableSpanStart",
    "clickableSpanEnd",
    "clickableSpanListener",
    "clickableSpanBold",
    requireAll = false
)
fun TextView.bindClickableSpan(
    spanStart: String? = null,
    spanEnd: String? = null,
    spanListener: OnTextViewClickableSpanListener? = null,
    spanBold: Boolean = false
) {
    if (text.isNullOrBlank()) return
    if (spanListener == null) return

    val builder = SpannableStringBuilder(text)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            spanListener.onClickableSpanEndClicked(view)
            view.invalidate()
        }

        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = context.themeColor(R.attr.colorPrimary)
            if (spanBold) textPaint.isFakeBoldText = true
        }
    }

    spanStart?.let {
        builder.insert(0, it)
        builder.setSpan(
            clickableSpan,
            0,
            it.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    spanEnd?.let {
        builder.append(it)
        builder.setSpan(
            clickableSpan,
            builder.length - it.length,
            builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    movementMethod = LinkMovementMethod.getInstance()
    setText(builder, TextView.BufferType.SPANNABLE)
}


/*
/**
 * Set a Chip's leading icon using Glide.
 *
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

@BindingAdapter("srcCompat")
fun ShapeableImageView.bindSrcCompat(@DrawableRes drawableRes: Int?) {
    if (drawableRes == null) return

    val drawable = context.getDrawableOrNull(drawableRes)
    setImageDrawable(drawable)
}

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
    @DrawableRes src: Int,
    centerCrop: Boolean,
    circularCrop: Boolean
): RequestBuilder<Drawable> {
    val req = Glide.with(context).load(src)
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
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

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
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

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