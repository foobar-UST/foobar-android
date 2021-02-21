package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ParticipantsExpandBinding
import com.foobarust.android.databinding.ParticipantsItemBinding
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
            R.layout.participants_item -> ParticipantsItemViewHolder(
                ParticipantsItemBinding.inflate(inflater, parent, false)
            )

            R.layout.participants_expand -> ParticipantsExpandViewHolder(
                ParticipantsExpandBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        when (holder) {
            is ParticipantsItemViewHolder -> holder.binding.run {
                userItem = getItem(position) as ParticipantsItemModel
                listener = this@ParticipantsAdapter.listener
                executePendingBindings()
            }

            is ParticipantsExpandViewHolder -> holder.binding.run {
                sectionId = this@ParticipantsAdapter.sectionId
                listener = this@ParticipantsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ParticipantsItemModel -> R.layout.participants_item
            is ParticipantsExpandModel -> R.layout.participants_expand
        }
    }

    override fun submitList(list: List<ParticipantsListModel>?) {
        val mergedList = if (!list.isNullOrEmpty()) list + ParticipantsExpandModel else list
        super.submitList(mergedList)
    }

    interface ParticipantsAdapterListener {
        fun onParticipantItemClicked(userId: String)
        fun onParticipantsExpandClicked(sectionId: String)
    }
}

sealed class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class ParticipantsItemViewHolder(
        val binding: ParticipantsItemBinding
    ) : ParticipantsViewHolder(binding.root)

    data class ParticipantsExpandViewHolder(
        val binding: ParticipantsExpandBinding
    ) : ParticipantsViewHolder(binding.root)
}

sealed class ParticipantsListModel {
    data class ParticipantsItemModel(
        val userPublic: UserPublic
    ) : ParticipantsListModel()

    object ParticipantsExpandModel : ParticipantsListModel()
}

object ParticipantsListModelDiff : DiffUtil.ItemCallback<ParticipantsListModel>() {
    override fun areItemsTheSame(
        oldItem: ParticipantsListModel,
        newItem: ParticipantsListModel
    ): Boolean {
        return when {
            oldItem is ParticipantsItemModel && newItem is ParticipantsItemModel ->
                oldItem.userPublic.username == newItem.userPublic.username
            oldItem is ParticipantsExpandModel && newItem is ParticipantsExpandModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: ParticipantsListModel,
        newItem: ParticipantsListModel
    ): Boolean {
        return when {
            oldItem is ParticipantsItemModel && newItem is ParticipantsItemModel ->
                oldItem.userPublic == newItem.userPublic
            oldItem is ParticipantsExpandModel && newItem is ParticipantsExpandModel ->
                true
            else -> false
        }
    }
}