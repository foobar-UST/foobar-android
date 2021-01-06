package com.foobarust.android.utils

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by kevin on 8/14/20
 */

suspend fun ViewGroup.setBottomSheetPeekTo(
    behavior: BottomSheetBehavior<*>,
    anchorView: View
) {
    // Hide bottom sheet at the beginning
    behavior.peekHeight = 0

    return suspendCancellableCoroutine { continuation ->
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                // Set peek height
                behavior.setPeekHeight(anchorView.bottom, true)
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)

        continuation.invokeOnCancellation {
           viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Drawable.setTintCompat(@ColorInt color: Int) {
    DrawableCompat.setTint(
        DrawableCompat.wrap(this),
        color
    )
}

fun AppBarLayout.doOnOffsetChanged(): Flow<AppBarStateChangedListener.State> = channelFlow {
    val listener = object : AppBarStateChangedListener() {
        override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
            channel.offer(state)
        }
    }.also {
        addOnOffsetChangedListener(it)
    }

    awaitClose { removeOnOffsetChangedListener(listener) }
}

fun RecyclerView.scrollToTop() = scrollToPosition(0)

fun RecyclerView.smoothScrollToTop() = smoothScrollToPosition(0)

suspend fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.scrollToTopWhenFirstItemInserted(
    recyclerView: RecyclerView
) {
    return suspendCancellableCoroutine { continuation ->
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                // Check if the new item is inserted in the first position
                if (positionStart == 0 &&
                    positionStart == layoutManager.findFirstCompletelyVisibleItemPosition()
                ) {
                    // Flicking if using the smooth version, async list animation will do the rest
                    recyclerView.scrollToTop()
                    unregisterAdapterDataObserver(this)
                    continuation.resume(Unit)
                }
            }
        }

        registerAdapterDataObserver(observer)

        continuation.invokeOnCancellation {
            unregisterAdapterDataObserver(observer)
        }
    }
}

fun EditText.setMaxLength(length: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
}

fun BottomNavigationView.hideIf(isHide: Boolean) {
    if (isHide) {
        animate().translationY(height.toFloat())
    } else {
        animate().translationY(0f)
    }
}

fun Activity.showShortToast(message: String?) {
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.showLongToast(message: String?) {
    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.showShortToast(message: String?) {
    message?.let {
        Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showLongToast(message: String?) {
    message?.let {
        Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
    }
}

fun RecyclerView.drawDivider(@LayoutRes forViewType: Int) {
    addItemDecoration(DividerDecoration(context, forViewType))
}

private class DividerDecoration(
    private val context: Context,
    @LayoutRes private val forViewType: Int
) : RecyclerView.ItemDecoration() {

    private var paint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = context.themeColor(R.attr.colorOnSurface)
        alpha = (context.themeFloat(R.attr.dividerAlphaRatio) * 255).toInt()
    }

    private var height = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1.toFloat(),                                // divider height 1dp
        context.resources.displayMetrics
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val viewType = parent.adapter?.getItemViewType(position)

        if (viewType == forViewType) {
            outRect.set(0, 0, 0, height.toInt())
        } else {
            outRect.setEmpty()
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val viewType = parent.adapter!!.getItemViewType(position)

            if (viewType == forViewType) {
                c.drawRect(
                    view.left.toFloat(),
                    view.bottom.toFloat(),
                    view.right.toFloat(),
                    view.bottom + height,
                    paint
                )
            }
        }
    }
}