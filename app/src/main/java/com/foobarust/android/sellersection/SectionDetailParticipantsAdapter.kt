package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SectionDetailParticipantsShowMoreItemBinding
import com.foobarust.android.databinding.SectionDetailParticipantsUserItemBinding
import com.foobarust.android.sellersection.SectionDetailParticipantsListModel.*
import com.foobarust.android.sellersection.SectionDetailParticipantsViewHolder.*
import com.foobarust.domain.models.user.UserPublic

/**
 * Created by kevin on 12/27/20
 */

class SectionDetailParticipantsAdapter(
    private val sectionId: String,
    private val listener: SectionDetailParticipantsAdapterListener
) : ListAdapter<SectionDetailParticipantsListModel, SectionDetailParticipantsViewHolder>(SectionDetailParticipantsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionDetailParticipantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_detail_participants_user_item -> SectionDetailParticipantsUserItemViewHolder(
                SectionDetailParticipantsUserItemBinding.inflate(inflater, parent, false)
            )

            R.layout.section_detail_participants_show_more_item -> SectionDetailParticipantsShowMoreItemViewHolder(
                SectionDetailParticipantsShowMoreItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionDetailParticipantsViewHolder, position: Int) {
        when (holder) {
            is SectionDetailParticipantsUserItemViewHolder -> holder.binding.run {
                userItem = getItem(position) as SectionDetailParticipantsUserItem
                listener = this@SectionDetailParticipantsAdapter.listener
                executePendingBindings()
            }

            is SectionDetailParticipantsShowMoreItemViewHolder -> holder.binding.run {
                sectionId = this@SectionDetailParticipantsAdapter.sectionId
                listener = this@SectionDetailParticipantsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionDetailParticipantsUserItem -> R.layout.section_detail_participants_user_item
            is SectionDetailParticipantsShowMoreItem -> R.layout.section_detail_participants_show_more_item
        }
    }

    override fun submitList(list: List<SectionDetailParticipantsListModel>?) {
        val mergedList = if (list != null) list + SectionDetailParticipantsShowMoreItem else list
        super.submitList(mergedList)
    }

    interface SectionDetailParticipantsAdapterListener {
        fun onParticipantItemClicked(userId: String)
        fun onParticipantsShowMoreClicked(sectionId: String)
    }
}

sealed class SectionDetailParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class SectionDetailParticipantsUserItemViewHolder(
        val binding: SectionDetailParticipantsUserItemBinding
    ) : SectionDetailParticipantsViewHolder(binding.root)

    data class SectionDetailParticipantsShowMoreItemViewHolder(
        val binding: SectionDetailParticipantsShowMoreItemBinding
    ) : SectionDetailParticipantsViewHolder(binding.root)
}

sealed class SectionDetailParticipantsListModel {
    data class SectionDetailParticipantsUserItem(
        val userPublic: UserPublic
    ) : SectionDetailParticipantsListModel()

    object SectionDetailParticipantsShowMoreItem : SectionDetailParticipantsListModel()
}

object SectionDetailParticipantsListModelDiff : DiffUtil.ItemCallback<SectionDetailParticipantsListModel>() {
    override fun areItemsTheSame(
        oldItem: SectionDetailParticipantsListModel,
        newItem: SectionDetailParticipantsListModel
    ): Boolean {
        return when {
            oldItem is SectionDetailParticipantsUserItem && newItem is SectionDetailParticipantsUserItem ->
                oldItem.userPublic.username == newItem.userPublic.username
            oldItem is SectionDetailParticipantsShowMoreItem && newItem is SectionDetailParticipantsShowMoreItem ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SectionDetailParticipantsListModel,
        newItem: SectionDetailParticipantsListModel
    ): Boolean {
        return when {
            oldItem is SectionDetailParticipantsUserItem && newItem is SectionDetailParticipantsUserItem ->
                oldItem.userPublic == newItem.userPublic
            oldItem is SectionDetailParticipantsShowMoreItem && newItem is SectionDetailParticipantsShowMoreItem ->
                true
            else -> false
        }
    }
}