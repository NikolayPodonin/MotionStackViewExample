package com.podonin.motionstackviewexample.adapter.stack

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.podonin.motionstackviewexample.R
import com.podonin.motionstackviewexample.databinding.ItemStackViewBinding
import com.podonin.motionstackviewexample.extensions.dpToPxFloat

class StackCardView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var binding =
        ItemStackViewBinding.inflate(
            LayoutInflater.from(context), this
        ).apply {
            id = MotionLayout.generateViewId()
        }

    init {
        clipChildren = false
        clipToPadding = false
        cardElevation = context.dpToPxFloat(12f)
        radius = context.dpToPxFloat(12f)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        rippleColor = ColorStateList.valueOf(ContextCompat.getColor(context, com.google.android.material.R.color.m3_button_ripple_color))
    }

    fun render(state: StackElement) {
        binding.stackText.text = state.text
    }
}