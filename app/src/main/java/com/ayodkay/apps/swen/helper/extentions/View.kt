package com.ayodkay.apps.swen.helper.extentions

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:isVisible")
fun View.isVisible(value: Boolean) {
    visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("app:accessibleTouchTarget")
fun View.accessibleTouchTarget(value: Boolean) {
    if (value) {
        post {
            val delegateArea = Rect()
            getHitRect(delegateArea)

            // 48 dp is the minimum requirement. We need to convert this to pixels.
            val accessibilityMin = context.dpToPx(48)

            // Calculate size vertically, and adjust touch area if it's smaller then the minimum.
            val height = delegateArea.bottom - delegateArea.top
            if (accessibilityMin > height) {
                // Add +1 px just in case min - height is odd and will be rounded down
                val addition = ((accessibilityMin - height) / 2).toInt() + 1
                delegateArea.top -= addition
                delegateArea.bottom += addition
            }

            // Calculate size horizontally, and adjust touch area if it's smaller then the minimum.
            val width = delegateArea.right - delegateArea.left
            if (accessibilityMin > width) {
                // Add +1 px just in case min - width is odd and will be rounded down
                val addition = ((accessibilityMin - width) / 2).toInt() + 1
                delegateArea.left -= addition
                delegateArea.right += addition
            }

            val parentView = parent as? View
            parentView?.touchDelegate = TouchDelegate(delegateArea, this)
        }
    }
}

// Extension function to convert dp to px.
fun Context.dpToPx(value: Int) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        resources.displayMetrics
    )
