package com.podonin.motionstackviewexample.widget

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Space
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.motion.widget.TransitionBuilder
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.podonin.motionstackviewexample.R
import com.podonin.motionstackviewexample.extensions.dpToPx
import com.podonin.motionstackviewexample.extensions.getColor

class MotionStackView : MotionLayout {

    private var views: List<View> = emptyList()
    lateinit var buttonHide: AppCompatTextView
    private lateinit var cardShiftSpace: Space

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        addButtonCollapse()
        addBarrier()
    }

    private fun createTransition(scene: MotionScene): MotionScene.Transition {
        val startSetId = View.generateViewId()
        val startSet = ConstraintSet()
        startSet.clone(this)
        val endSetId = View.generateViewId()
        val endSet = ConstraintSet()
        endSet.clone(this)
        val transitionId = View.generateViewId()
        return TransitionBuilder.buildTransition(
            scene,
            transitionId,
            startSetId, startSet,
            endSetId, endSet
        ).also {
            it.duration = 300
        }
    }

    fun setData(stacked: List<View>) {
        if (stacked == views) {
            buttonHide.isVisible = false
            return
        }

        buttonHide.isVisible = true

        views.forEach(::removeView)

        views = stacked

        stacked.forEachIndexedReversed { index, view ->
            val layoutParams = LayoutParams(ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
            addView(view, layoutParams)
            if (index != stacked.count() - 1 && view is CardView) {
                view.cardElevation -= 1
            }
        }
        val dataTransition = createSceneAndTransition()
        setTransition(dataTransition)
        collapseItems()
    }

    fun expandItems() {
        transitionToEnd()
        viewsEnableClickable()
    }

    fun collapseItems() {
        transitionToStart()
        if (views.count() > 1) {
            viewsDisableClickable()
        }
    }

    fun forceExpandItems() {
        progress = 1f
        viewsEnableClickable()
    }

    fun forceCollapseItems() {
        progress = 0f
        viewsDisableClickable()
    }

    private fun viewsDisableClickable() {
        views.forEach {
            it.isClickable = false
            it.isFocusable = false
        }
    }

    private fun viewsEnableClickable() {
        views.forEach {
            it.isClickable = true
            it.isFocusable = true
        }
    }

    private fun addButtonCollapse() {
        buttonHide = AppCompatTextView(context).apply {
            id = generateViewId()
            text = context.getString(R.string.collapse)
            val insetDrawable = InsetDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_collapse),
                0,
                0,
                dpToPx(6f),
                0
            )
            setCompoundDrawablesWithIntrinsicBounds(
                insetDrawable, null, null, null
            )
            setOnClickListener {
                collapseItems()
            }
            isAllCaps = true
            textSize = 14f
            setPadding(dpToPx(8f))
            setTextColor(getColor(R.color.black))
        }
        val layoutParams = LayoutParams(ConstraintSet.MATCH_CONSTRAINT, ConstraintSet.WRAP_CONTENT)
        addView(buttonHide, layoutParams)
    }

    private fun addBarrier() {
        cardShiftSpace = Space(context).apply {
            id = generateViewId()
        }
        addView(cardShiftSpace, LayoutParams(ConstraintSet.WRAP_CONTENT, ConstraintSet.WRAP_CONTENT))
    }

    private fun createSceneAndTransition(): MotionScene.Transition {
        val scene = MotionScene(this)
        val dataTransition = createTransition(scene)

        scene.addTransition(dataTransition)
        scene.setTransition(dataTransition)
        setScene(scene)

        val startSet = getConstraintSet(dataTransition.startConstraintSetId)
        updateCollapsedSet(startSet)

        val endSet = getConstraintSet(dataTransition.endConstraintSetId)
        updateExpandedSet(endSet)

        startSet.applyTo(this)

        return dataTransition
    }

    private fun updateCollapsedSet(set: ConstraintSet) {
        if (views.isEmpty()) {
            return
        }

        set.connect(buttonHide.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(buttonHide.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 10)

        set.connect(views[0].id, ConstraintSet.TOP, buttonHide.id, ConstraintSet.TOP)
        set.connect(views[0].id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
        set.connect(views[0].id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)

        val secondCardShift = dpToPx(6f)

        set.connect(
            cardShiftSpace.id,
            ConstraintSet.TOP,
            views[0].id,
            ConstraintSet.BOTTOM,
            secondCardShift
        )

        for (i in 1 until views.size) {
            val id = views[i].id
            set.connect(
                id,
                ConstraintSet.TOP,
                views[0].id,
                ConstraintSet.TOP,
                secondCardShift
            )
            set.connect(
                id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            set.connect(
                id,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            set.connect(
                id,
                ConstraintSet.BOTTOM,
                cardShiftSpace.id,
                ConstraintSet.BOTTOM
            )

            set.constrainHeight(id, ConstraintSet.MATCH_CONSTRAINT)
            set.setScaleX(id, 0.96f)
            if (i != 1) {
                set.setAlpha(id, 0f)
            }
        }
    }

    private fun updateExpandedSet(set: ConstraintSet) {
        if (views.isEmpty()) {
            return
        }

        set.connect(buttonHide.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(buttonHide.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 10)

        set.connect(views[0].id, ConstraintSet.TOP, buttonHide.id, ConstraintSet.BOTTOM, dpToPx(24f))
        set.connect(views[0].id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
        set.connect(views[0].id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
        for (i in 1 until views.size) {
            val id = views[i].id
            set.connect(
                id,
                ConstraintSet.TOP,
                views[i - 1].id,
                ConstraintSet.BOTTOM,
                dpToPx(16f)
            )
            set.connect(
                id,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT,
                0
            )
            set.connect(
                id,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT,
                0
            )
            set.setScaleX(id, 1f)

            set.constrainHeight(id, ConstraintSet.WRAP_CONTENT)
            if (i != 1) {
                set.setAlpha(id, 1f)
            }
        }
    }

    private inline fun <T> Iterable<T>.forEachIndexedReversed(action: (index: Int, T) -> Unit) {
        val lastIndex = this.count() - 1
        for (i in lastIndex downTo 0) action.invoke(lastIndex - i, this.elementAt(i))
    }
}