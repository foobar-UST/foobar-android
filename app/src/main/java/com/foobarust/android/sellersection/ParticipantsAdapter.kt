package com.foobarust.android.sellersection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ParticipantsAvatarItemBinding
import com.foobarust.android.databinding.ParticipantsExpandBinding
import com.foobarust.android.databinding.ParticipantsListItemBinding
import com.foobarust.android.sellersection.ParticipantsListModel.*
import com.foobarust.android.sellersection.ParticipantsViewHolder.*
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.domain.models.user.UserPublic

/**
 * Created by kevin on 12/27/20
 */

class ParticipantsAdapter(
    private val listener: ParticipantsAdapterListener? = null
) : ListAdapter<ParticipantsListModel, ParticipantsViewHolder>(ParticipantsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.participants_avatar_item -> ParticipantsAvatarItemViewHolder(
                ParticipantsAvatarItemBinding.inflate(inflater, parent, false)
            )
            R.layout.participants_list_item -> ParticipantsListItemVieHolder(
                ParticipantsListItemBinding.inflate(inflater, parent, false)
            )
            R.layout.participants_expand -> ParticipantsExpandViewHolder(
                ParticipantsExpandBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        when (holder) {
            is ParticipantsAvatarItemViewHolder -> bindParticipantsAvatarItem(
                binding = holder.binding,
                avatarItemModel = getItem(position) as ParticipantsAvatarItemModel
            )
            is ParticipantsListItemVieHolder -> bindParticipantsListItem(
                binding = holder.binding,
                listItemModel = getItem(position) as ParticipantsListItemModel
            )
            is ParticipantsExpandViewHolder -> bindParticipantsExpand(
                binding = holder.binding
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ParticipantsAvatarItemModel -> R.layout.participants_avatar_item
            is ParticipantsListItemModel -> R.layout.participants_list_item
            is ParticipantsExpandModel -> R.layout.participants_expand
        }
    }

    private fun bindParticipantsAvatarItem(
        binding: ParticipantsAvatarItemBinding,
        avatarItemModel: ParticipantsAvatarItemModel
    ) = binding.run {
        userPhotoImageView.loadGlideUrl(
            imageUrl = avatarItemModel.userPublic.photoUrl,
            centerCrop = true,
            placeholder = R.drawable.ic_user
        )

        userUsernameTextView.text = avatarItemModel.userPublic.username
    }

    private fun bindParticipantsListItem(
        binding: ParticipantsListItemBinding,
        listItemModel: ParticipantsListItemModel
    ) = binding.run {
        userPhotoImageView.loadGlideUrl(
            imageUrl = listItemModel.userPublic.photoUrl,
            centerCrop = true,
            placeholder = R.drawable.ic_user
        )

        userUsernameTextView.text = listItemModel.userPublic.username
    }

    private fun bindParticipantsExpand(
        binding: ParticipantsExpandBinding
    ) = binding.run {
        listener?.let { listener ->
            showMoreButton.setOnClickListener { listener.onExpandParticipants() }
        }
    }

    interface ParticipantsAdapterListener {
        fun onExpandParticipants()
    }
}

sealed class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class ParticipantsAvatarItemViewHolder(
        val binding: ParticipantsAvatarItemBinding
    ) : ParticipantsViewHolder(binding.root)

    data class ParticipantsListItemVieHolder(
        val binding: ParticipantsListItemBinding
    ) : ParticipantsViewHolder(binding.root)

    data class ParticipantsExpandViewHolder(
        val binding: ParticipantsExpandBinding
    ) : ParticipantsViewHolder(binding.root)
}

sealed class ParticipantsListModel {
    data class ParticipantsAvatarItemModel(
        val userPublic: UserPublic
    ) : ParticipantsListModel()

    data class ParticipantsListItemModel(
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
            oldItem is ParticipantsAvatarItemModel && newItem is ParticipantsAvatarItemModel ->
                oldItem.userPublic.id == newItem.userPublic.id
            oldItem is ParticipantsListItemModel && newItem is ParticipantsListItemModel ->
                oldItem.userPublic.id == newItem.userPublic.id
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
            oldItem is ParticipantsAvatarItemModel && newItem is ParticipantsAvatarItemModel ->
                oldItem == newItem
            oldItem is ParticipantsListItemModel && newItem is ParticipantsListItemModel ->
                oldItem == newItem
            oldItem is ParticipantsExpandModel && newItem is ParticipantsExpandModel ->
                true
            else -> false
        }
    }
}