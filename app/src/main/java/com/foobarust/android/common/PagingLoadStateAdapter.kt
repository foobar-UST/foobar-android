package com.foobarust.android.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.databinding.PagingLoadStateItemBinding

/**
 * Created by kevin on 9/28/20
 */

class PagingLoadStateAdapter(
    private val retryAction: () -> Unit
) : LoadStateAdapter<PagingLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: PagingLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PagingLoadStateViewHolder(
            PagingLoadStateItemBinding.inflate(inflater, parent, false),
            retryAction
        )
    }
}

class PagingLoadStateViewHolder(
    private val binding: PagingLoadStateItemBinding,
    private val retryAction: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.run {
            this.loadState = loadState
            this.retryAction = this@PagingLoadStateViewHolder.retryAction
            executePendingBindings()
        }
    }
}