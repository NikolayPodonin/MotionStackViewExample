package com.podonin.motionstackviewexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.podonin.motionstackviewexample.R
import com.podonin.motionstackviewexample.adapter.stack.StackViewHolder
import com.podonin.motionstackviewexample.adapter.stub.StubViewHolder

class Adapter(
    private val onStackClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = mutableListOf<Item>()

    fun add(position: Int, items: List<Item>) {
        data.addAll(position, items)
        notifyItemRangeInserted(position, items.size)
    }

    fun getItem(position: Int): Item = data[position]

    fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.item_motion_stack -> StackViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                viewType,
                parent,
                false
            ), onStackClick
        )
        R.layout.item_stub_view -> StubViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                viewType,
                parent,
                false
            )
        )
        else -> throw IllegalStateException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StackViewHolder) {
            holder.bind((data[position] as MotionStackItem).elements)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is MotionStackItem -> R.layout.item_motion_stack
            StubItem -> R.layout.item_stub_view
            else -> throw IllegalStateException()
        }
    }
}

sealed interface Item