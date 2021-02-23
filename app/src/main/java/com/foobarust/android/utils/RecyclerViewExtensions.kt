package com.foobarust.android.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by kevin on 1/27/21
 */

fun RecyclerView.touchOutsideItems(): Flow<Unit> = callbackFlow {
    val listener = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action != MotionEvent.ACTION_UP) return false

            val child = rv.findChildViewUnder(e.x, e.y)

            return if (child != null) {
                false
            } else {
                channel.offer(Unit)
                true
            }
        }
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit
    }

    addOnItemTouchListener(listener)

    awaitClose { removeOnItemTouchListener(listener) }
}

fun RecyclerView.scrollToTop() = scrollToPosition(0)

fun RecyclerView.smoothScrollToTop() = smoothScrollToPosition(0)

fun RecyclerView.drawDivider(@LayoutRes forViewType: Int) {
    addItemDecoration(DividerDecoration(context, forViewType))
}

fun RecyclerView.drawItemMargin(
    marginSize: Int,
    horizontalMargin: Boolean = false,
    includeVerticalEnds: Boolean = true,
    forViewType: Int? = null
) {
    addItemDecoration(
        MarginItemDecoration(marginSize, horizontalMargin, includeVerticalEnds, forViewType)
    )
}

/**
 * Emit number of visible items when [RecyclerView]'s [LinearLayoutManager] has finished drawing.
 */
suspend fun RecyclerView.layoutCompletedFlow(): Flow<Int> = callbackFlow {
    val layoutManager = object : LinearLayoutManager(
        context,
        VERTICAL,
        false
    ) {
        override fun onLayoutCompleted(state: RecyclerView.State?) {
            super.onLayoutCompleted(state)
            val firstVisibleItemPosition = findFirstVisibleItemPosition()
            val lastVisibleItemPosition = findLastVisibleItemPosition()
            val itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1

            channel.offer(itemsShown)
        }
    }

    this@layoutCompletedFlow.layoutManager = layoutManager

    awaitClose { }
}

/**
 * Scroll to top when there is new items inserted at the top of [RecyclerView].
 */
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

private class MarginItemDecoration(
    private val spaceSize: Int,
    private val horizontalMargin: Boolean,
    private val includeVerticalEnds: Boolean = true,
    private val forViewType: Int? = null
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Get item view type
        if (forViewType != null) {
            val position = parent.getChildAdapterPosition(view)
            val itemViewType = parent.adapter!!.getItemViewType(position)

            if (itemViewType != forViewType) {
                return
            }
        }

        // Draw margin
        with(outRect) {
            val isFirstItem = parent.getChildAdapterPosition(view) == 0
            if (isFirstItem && includeVerticalEnds) {
                top = spaceSize
            }

            val isLastItem = parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1
            if (!isLastItem || (isLastItem && includeVerticalEnds)) {
                bottom = spaceSize
            }

            if (horizontalMargin) {
                left = spaceSize
                right = spaceSize
            }
        }
    }
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