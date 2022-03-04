package com.podonin.motionstackviewexample.extensions

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


fun View.getColor(@ColorRes res: Int) = ContextCompat.getColor(context, res)

fun Context.dpToPx(dp: Float): Int {
    return dpToPxFloat(dp).toInt()
}
fun View.dpToPx(dp: Float): Int {
    return context.dpToPxFloat(dp).toInt()
}

fun Context.dpToPxFloat(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}