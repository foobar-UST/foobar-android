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
import com.foobarust.android.databinding.ProfileWarningItemBinding
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

            R.layout.profile_warning_item -> ProfileWarningViewHolder(
                ProfileWarningItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        when (holder) {
            is ProfileInfoViewHolder -> holder.binding.run {
                infoModel = getItem(position) as ProfileInfoModel
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileEditViewHolder -> holder.binding.run {
                editModel = getItem(position) as ProfileEditModel
                listener = this@ProfileAdapter.listener
                executePendingBindings()
            }

            is ProfileWarningViewHolder -> holder.binding.run {
                warningModel = getItem(position) as ProfileWarningModel
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProfileInfoModel -> R.layout.profile_info_item
            is ProfileEditModel -> R.layout.profile_edit_item
            is ProfileWarningModel -> R.layout.profile_warning_item
        }
    }

    interface ProfileAdapterListener {
        fun onProfileAvatarClicked()
        fun onProfileEditItemClicked(editModel: ProfileEditModel)
    }
}

sealed class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ProfileInfoViewHolder(
        val binding: ProfileInfoItemBinding
    ) : ProfileViewHolder(binding.root)

    class ProfileEditViewHolder(
        val binding: ProfileEditItemBinding
    ) : ProfileViewHolder(binding.root)

    class ProfileWarningViewHolder(
        val binding: ProfileWarningItemBinding
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