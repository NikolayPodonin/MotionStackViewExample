package com.podonin.motionstackviewexample.adapter.stack

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.podonin.motionstackviewexample.extensions.dpToPx

class StackTextView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        id = MotionLayout.generateViewId()
        val padding = dpToPx(16f)
        setPadding(padding, padding, padding, padding)
    }
}