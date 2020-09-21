package com.foobarust.android.utils

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.InputFilter
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Created by kevin on 8/14/20
 */

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
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.showLongToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showShortToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String?) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
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
        color = ContextCompat.getColor(context, R.color.material_on_surface_stroke)
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