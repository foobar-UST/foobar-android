package com.foobarust.android.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ItemProfileEditBinding
import com.foobarust.android.databinding.ItemProfileInfoBinding
import com.foobarust.android.databinding.ItemProfileWarningBinding
import com.foobarust.android.profile.ProfileAdapter.ProfileAdapterListener
import com.foobarust.android.profile.ProfileListModel.*
import com.foobarust.android.profile.ProfileViewHolder.*
import com.foobarust.domain.models.UserDetail

class ProfileAdapter(
    private val listener: ProfileAdapterListener
) : ListAdapter<ProfileListModel, ProfileViewHolder>(ProfileListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_profile_info -> ProfileInfoViewHolder(
                ItemProfileInfoBinding.inflate(inflater, parent, false),
                listener
            )

            R.layout.item_profile_edit -> ProfileEditViewHolder(
                ItemProfileEditBinding.inflate(inflater, parent, false),
                listener
            )

            R.layout.item_profile_warning -> ProfileWarningViewHolder(
                ItemProfileWarningBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        when (holder) {
            is ProfileInfoViewHolder -> holder.binding.run {
                userModel = (getItem(position) as ProfileInfoModel).userDetail
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileEditViewHolder -> holder.binding.run {
                profileEditModel = getItem(position) as ProfileEditModel
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileWarningViewHolder -> holder.binding.run {
                message = (getItem(position) as ProfileWarningModel).message
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProfileInfoModel -> R.layout.item_profile_info
            is ProfileEditModel -> R.layout.item_profile_edit
            is ProfileWarningModel -> R.layout.item_profile_warning
        }
    }

    interface ProfileAdapterListener {
        fun onProfileAvatarClicked()
        fun onProfileEditItemClicked(editModel: ProfileEditModel)
    }
}

sealed class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ProfileInfoViewHolder(
        val binding: ItemProfileInfoBinding,
        val listener: ProfileAdapterListener
    ) : ProfileViewHolder(binding.root)

    class ProfileEditViewHolder(
        val binding: ItemProfileEditBinding,
        val listener: ProfileAdapterListener
    ) : ProfileViewHolder(binding.root)

    class ProfileWarningViewHolder(
        val binding: ItemProfileWarningBinding
    ) : ProfileViewHolder(binding.root)
}

sealed class ProfileListModel {
    data class ProfileInfoModel(
        val userDetail: UserDetail
    ) : ProfileListModel()

    data class ProfileEditModel(
        val id: String,
        val title: String,
        val value: String?,
        val displayValue: String?
    ) : ProfileListModel()

    data class ProfileWarningModel(
        val message: String
    ) : ProfileListModel()
}

object ProfileListModelDiff : DiffUtil.ItemCallback<ProfileListModel>() {
    override fun areItemsTheSame(oldItem: ProfileListModel, newItem: ProfileListModel): Boolean {
        return when {
            oldItem is ProfileInfoModel && newItem is ProfileInfoModel -> true
            oldItem is ProfileEditModel && newItem is ProfileEditModel -> true
            oldItem is ProfileWarningModel && newItem is ProfileWarningModel -> oldItem.message == newItem.message
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ProfileListModel, newItem: ProfileListModel): Boolean {
        return when {
            oldItem is ProfileInfoModel && newItem is ProfileInfoModel ->
                oldItem.userDetail.username == newItem.userDetail.username &&
                oldItem.userDetail.email == newItem.userDetail.email &&
                oldItem.userDetail.photoUrl == newItem.userDetail.photoUrl
            oldItem is ProfileEditModel && newItem is ProfileEditModel -> oldItem == newItem
            oldItem is ProfileWarningModel && newItem is ProfileWarningModel -> oldItem.message == newItem.message
            else -> false
        }
    }
}