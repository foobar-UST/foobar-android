package com.foobarust.android.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ProfileEditItemBinding
import com.foobarust.android.databinding.ProfileInfoItemBinding
import com.foobarust.android.databinding.ProfileNoticeItemBinding
import com.foobarust.android.settings.ProfileListModel.*
import com.foobarust.android.settings.ProfileViewHolder.*
import com.foobarust.domain.models.user.UserDetail

class ProfileAdapter(
    private val listener: ProfileAdapterListener
) : ListAdapter<ProfileListModel, ProfileViewHolder>(ProfileListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.profile_info_item -> ProfileInfoViewHolder(
                ProfileInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.profile_edit_item -> ProfileEditViewHolder(
                ProfileEditItemBinding.inflate(inflater, parent, false)
            )
            R.layout.profile_notice_item -> ProfileNoticeViewHolder(
                ProfileNoticeItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        when (holder) {
            is ProfileInfoViewHolder -> holder.binding.run {
                infoItemModel = getItem(position) as ProfileInfoItemModel
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileEditViewHolder -> holder.binding.run {
                editItemModel = getItem(position) as ProfileEditItemModel
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileNoticeViewHolder -> holder.binding.run {
                noticeItemModel = getItem(position) as ProfileNoticeItemModel
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProfileInfoItemModel -> R.layout.profile_info_item
            is ProfileEditItemModel -> R.layout.profile_edit_item
            is ProfileNoticeItemModel -> R.layout.profile_notice_item
        }
    }

    interface ProfileAdapterListener {
        fun onProfileAvatarClicked()
        fun onProfileEditItemClicked(editItemModel: ProfileEditItemModel)
    }
}

sealed class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ProfileInfoViewHolder(
        val binding: ProfileInfoItemBinding
    ) : ProfileViewHolder(binding.root)

    class ProfileEditViewHolder(
        val binding: ProfileEditItemBinding
    ) : ProfileViewHolder(binding.root)

    class ProfileNoticeViewHolder(
        val binding: ProfileNoticeItemBinding
    ) : ProfileViewHolder(binding.root)
}

sealed class ProfileListModel {
    data class ProfileInfoItemModel(
        val userDetail: UserDetail
    ) : ProfileListModel()

    data class ProfileEditItemModel(
        val id: String,
        val title: String,
        val value: String?,
        val displayValue: String?
    ) : ProfileListModel()

    data class ProfileNoticeItemModel(
        val message: String
    ) : ProfileListModel()
}

object ProfileListModelDiff : DiffUtil.ItemCallback<ProfileListModel>() {
    override fun areItemsTheSame(oldItem: ProfileListModel, newItem: ProfileListModel): Boolean {
        return when {
            oldItem is ProfileInfoItemModel && newItem is ProfileInfoItemModel -> true
            oldItem is ProfileEditItemModel && newItem is ProfileEditItemModel -> true
            oldItem is ProfileNoticeItemModel && newItem is ProfileNoticeItemModel -> oldItem.message == newItem.message
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ProfileListModel, newItem: ProfileListModel): Boolean {
        return when {
            oldItem is ProfileInfoItemModel && newItem is ProfileInfoItemModel ->
                oldItem.userDetail.username == newItem.userDetail.username &&
                oldItem.userDetail.email == newItem.userDetail.email &&
                oldItem.userDetail.photoUrl == newItem.userDetail.photoUrl
            oldItem is ProfileEditItemModel && newItem is ProfileEditItemModel -> oldItem == newItem
            oldItem is ProfileNoticeItemModel && newItem is ProfileNoticeItemModel -> oldItem.message == newItem.message
            else -> false
        }
    }
}