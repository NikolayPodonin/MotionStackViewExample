package com.podonin.motionstackviewexample

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.podonin.motionstackviewexample.adapter.Adapter
import com.podonin.motionstackviewexample.adapter.MotionStackItem
import com.podonin.motionstackviewexample.adapter.StubItem
import com.podonin.motionstackviewexample.adapter.stack.StackCardView
import com.podonin.motionstackviewexample.adapter.stack.StackElement
import com.podonin.motionstackviewexample.databinding.ActivityMainBinding
import com.podonin.motionstackviewexample.extensions.dpToPx
import com.podonin.motionstackviewexample.widget.MotionStackView

const val topPaddingItems: Float = 90f

class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val adapter = Adapter {
        expandItems()
    }
    private var itemsExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter

        // Fill screen with fake data
        adapter.add(0, listOf(StubItem, StubItem, StubItem, StubItem, StubItem))
        showItems(listOf(StackElement(), StackElement(), StackElement(), StackElement()))
    }

    private fun showItems(itemViewStates: List<StackElement>) {
        if (adapter.itemCount > 0 &&
            adapter.getItem(0) is MotionStackItem
        ) {
            adapter.removeItem(0)
        }

        if (itemViewStates.isEmpty()) return

        adapter.add(
            0,
            listOf(MotionStackItem(itemViewStates))
        )

        binding.recyclerView.scrollToPosition(0)

        val views = itemViewStates
            .map { item ->
                StackCardView(this).apply { render(item) }
            }

        binding.stackView.setData(views)

        if (itemsExpanded && itemViewStates.size > 1) {
            forceExpandItems()
        } else if (itemsExpanded) {
            collapseItems()
        }
    }


    private fun expandItems() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
            scrollToItemsAndExpandAfter()
            return
        }

        val rvEvView = getItemsItemView() ?: return
        val location = IntArray(2).apply(rvEvView::getLocationOnScreen)
        val containerLocation = IntArray(2).apply(binding.stackViewContainer::getLocationOnScreen)

        val relativePadding = (location[1] - containerLocation[1])

        binding.stackViewContainer.updatePadding(top = relativePadding)
        binding.stackViewContainer.isVisible = true

        changeVisibilityOfItemsItem(true)

        binding.whiteBg.alpha = 0f
        binding.whiteBg.isVisible = true

        binding.stackView.buttonHide.setOnClickListener {
            collapseItems()
        }
        binding.stackView.expandItems()
        binding.whiteBg.animate().alpha(1f)
        ValueAnimator.ofInt(relativePadding, dpToPx(topPaddingItems))
            .apply {
                addUpdateListener {
                    binding.stackViewContainer.updatePadding(top = it.animatedValue as Int)
                }
            }.start()

        itemsExpanded = true
    }

    private fun collapseItems() {
        val rvEvView = getItemsItemView() ?: return
        val location = IntArray(2).apply(rvEvView::getLocationOnScreen)
        val containerLocation = IntArray(2).apply(binding.stackViewContainer::getLocationOnScreen)

        val relativePadding = (location[1] - containerLocation[1])

        binding.stackView.collapseItems()

        binding.whiteBg.animate().alpha(0f)

        ValueAnimator.ofInt(binding.stackViewContainer.paddingTop, relativePadding)
            .apply {
                addUpdateListener {
                    binding.stackViewContainer.updatePadding(top = it.animatedValue as Int)
                }
                addListener(
                    onEnd = {
                        binding.whiteBg.isVisible = false
                        binding.stackViewContainer.isVisible = false

                        changeVisibilityOfItemsItem(false)
                    }
                )
            }.start()

        itemsExpanded = false
    }

    private fun scrollToItemsAndExpandAfter() {
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        lateinit var listener: RecyclerView.OnScrollListener
        listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    expandItems()
                    binding.recyclerView.removeOnScrollListener(listener)
                }
            }
        }
        binding.recyclerView.addOnScrollListener(listener)

        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0
        layoutManager.startSmoothScroll(smoothScroller)
    }

    private fun forceExpandItems() {
        binding.stackViewContainer.updatePadding(top = dpToPx(topPaddingItems))
        binding.stackViewContainer.isVisible = true
        binding.whiteBg.alpha = 1f
        binding.whiteBg.isVisible = true
        binding.stackView.forceExpandItems()

        changeVisibilityOfItemsItem(true)
    }

    private fun changeVisibilityOfItemsItem(isInvisible: Boolean) {
        getItemsItemView()?.let {
            it.isInvisible = isInvisible
            return
        }

        adapter.notifyItemChanged(0)
    }

    private fun getItemsItemView() =
        binding.recyclerView.findViewById<MotionStackView>(R.id.stackView)

}