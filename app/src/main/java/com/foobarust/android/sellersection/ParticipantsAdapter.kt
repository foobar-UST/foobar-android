package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ParticipantsShowMoreItemBinding
import com.foobarust.android.databinding.ParticipantsUserItemBinding
import com.foobarust.android.sellersection.ParticipantsListModel.*
import com.foobarust.android.sellersection.ParticipantsViewHolder.*
import com.foobarust.domain.models.user.UserPublic

/**
 * Created by kevin on 12/27/20
 */

class ParticipantsAdapter(
    private val sectionId: String,
    private val listener: ParticipantsAdapterListener
) : ListAdapter<ParticipantsListModel, ParticipantsViewHolder>(ParticipantsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.participants_user_item -> ParticipantsUserItemViewHolder(
                ParticipantsUserItemBinding.inflate(inflater, parent, false)
            )

            R.layout.participants_show_more_item -> ParticipantsShowMoreItemViewHolder(
                ParticipantsShowMoreItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        when (holder) {
            is ParticipantsUserItemViewHolder -> holder.binding.run {
                userItem = getItem(position) as ParticipantsUserItem
                listener = this@ParticipantsAdapter.listener
                executePendingBindings()
            }

            is ParticipantsShowMoreItemViewHolder -> holder.binding.run {
                sectionId = this@ParticipantsAdapter.sectionId
                listener = this@ParticipantsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ParticipantsUserItem -> R.layout.participants_user_item
            is ParticipantsShowMoreItem -> R.layout.participants_show_more_item
        }
    }

    interface ParticipantsAdapterListener {
        fun onParticipantItemClicked(userId: String)
        fun onParticipantsShowMoreClicked(sectionId: String)
    }
}

sealed class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class ParticipantsUserItemViewHolder(
        val binding: ParticipantsUserItemBinding
    ) : ParticipantsViewHolder(binding.root)

    data class ParticipantsShowMoreItemViewHolder(
        val binding: ParticipantsShowMoreItemBinding
    ) : ParticipantsViewHolder(binding.root)
}

sealed class ParticipantsListModel {
    data class ParticipantsUserItem(
        val userPublic: UserPublic
    ) : ParticipantsListModel()

    object ParticipantsShowMoreItem : ParticipantsListModel()
}

object ParticipantsListModelDiff : DiffUtil.ItemCallback<ParticipantsListModel>() {
    override fun areItemsTheSame(
        oldItem: ParticipantsListModel,
        newItem: ParticipantsListModel
    ): Boolean {
        return when {
            oldItem is ParticipantsUserItem && newItem is ParticipantsUserItem ->
                oldItem.userPublic.username == newItem.userPublic.username
            oldItem is ParticipantsShowMoreItem && newItem is ParticipantsShowMoreItem ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: ParticipantsListModel,
        newItem: ParticipantsListModel
    ): Boolean {
        return when {
            oldItem is ParticipantsUserItem && newItem is ParticipantsUserItem ->
                oldItem.userPublic == newItem.userPublic
            oldItem is ParticipantsShowMoreItem && newItem is ParticipantsShowMoreItem ->
                true
            else -> false
        }
    }
}