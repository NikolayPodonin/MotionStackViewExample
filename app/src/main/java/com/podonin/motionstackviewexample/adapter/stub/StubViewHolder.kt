package com.podonin.motionstackviewexample.adapter.stub

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.podonin.motionstackviewexample.databinding.ItemStubViewBinding

class StubViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    fun bind() {
        val binding = ItemStubViewBinding.bind(itemView)
    }
}