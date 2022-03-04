package com.podonin.motionstackviewexample.adapter.stack

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.podonin.motionstackviewexample.databinding.ItemMotionStackBinding

class StackViewHolder(
    itemView: View,
    val click: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    fun bind(elements: List<StackElement>) {
        val binding = ItemMotionStackBinding.bind(itemView)
        val views = elements.map { element ->
            StackCardView(itemView.context).apply { render(element) }
        }

        binding.stackView.setData(views)

        if (elements.count() > 1)
            binding.stackView.setOnClickListener {
                click()
            }
        else
            binding.stackView.setOnClickListener(null)

        binding.stackView.buttonHide.isVisible = false
    }
}