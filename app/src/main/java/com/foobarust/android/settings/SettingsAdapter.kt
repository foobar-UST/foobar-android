package com.foobarust.android.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SettingsAccountItemBinding
import com.foobarust.android.databinding.SettingsSectionItemBinding
import com.foobarust.android.settings.SettingsListModel.SettingsAccountItemModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionItemModel
import com.foobarust.android.settings.SettingsViewHolder.SettingsAccountViewHolder
import com.foobarust.android.settings.SettingsViewHolder.SettingsSectionViewHolder
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.setDrawables
import com.foobarust.android.utils.setSrc

class SettingsAdapter(
    private val listener: SettingsAdapterListener
) : ListAdapter<SettingsListModel, SettingsViewHolder>(SettingsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.settings_account_item -> SettingsAccountViewHolder(
                SettingsAccountItemBinding.inflate(inflater, parent, false)
            )

            R.layout.settings_section_item -> SettingsSectionViewHolder(
                SettingsSectionItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        when (holder) {
            is SettingsAccountViewHolder -> bindAccountItem(
                binding = holder.binding,
                accountItemModel = getItem(position) as SettingsAccountItemModel
            )
            is SettingsSectionViewHolder -> bindSectionItem(
                binding = holder.binding,
                sectionItemModel = getItem(position) as SettingsSectionItemModel
            )
        }
    }

    private fun bindAccountItem(
        binding: SettingsAccountItemBinding,
        accountItemModel: SettingsAccountItemModel
    ) = binding.run {
        val context = root.context

        // Profile image
        if (accountItemModel.signedIn && accountItemModel.photoUrl != null) {
            avatarImageView.loadGlideUrl(
                imageUrl = accountItemModel.photoUrl,
                centerCrop = true
            )
        } else {
            avatarImageView.setSrc(R.drawable.ic_user)
        }

        // Username
        usernameTextView.text = if (accountItemModel.signedIn) {
            accountItemModel.username
        } else {
            context.getString(R.string.settings_account_guest)
        }

        // Description
        descriptionTextView.text = if (accountItemModel.signedIn) {
            context.getString(R.string.settings_account_edit_profile)
        } else {
            context.getString(R.string.settings_account_sign_in)
        }

        accountItemLayout.setOnClickListener {
            listener.onProfileClicked(accountItemModel.signedIn)
        }
    }

    private fun bindSectionItem(
        binding: SettingsSectionItemBinding,
        sectionItemModel: SettingsSectionItemModel
    ) = binding.run {
        with(sectionTextView) {
            text = sectionItemModel.title
            setDrawables(drawableLeft = sectionItemModel.drawableRes)
            setOnClickListener {
                listener.onSectionItemClicked(sectionItemModel.id)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingsAccountItemModel -> R.layout.settings_account_item
            is SettingsSectionItemModel -> R.layout.settings_section_item
        }
    }

    interface SettingsAdapterListener {
        fun onProfileClicked(isSignedIn: Boolean)
        fun onSectionItemClicked(sectionId: String)
    }
}

sealed class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SettingsAccountViewHolder(
        val binding: SettingsAccountItemBinding
    ) : SettingsViewHolder(binding.root)

    class SettingsSectionViewHolder(
        val binding: SettingsSectionItemBinding
    ) : SettingsViewHolder(binding.root)
}

sealed class SettingsListModel {
    data class SettingsAccountItemModel(
        val signedIn: Boolean,
        val username: String? = null,
        val photoUrl: String? = null
    ) : SettingsListModel()

    data class SettingsSectionItemModel(
        val id: String,
        @DrawableRes val drawableRes: Int,
        val title: String
    ) : SettingsListModel()
}

object SettingsListModelDiff : DiffUtil.ItemCallback<SettingsListModel>() {
    override fun areItemsTheSame(oldItem: SettingsListModel, newItem: SettingsListModel): Boolean {
        return when {
            oldItem is SettingsAccountItemModel && newItem is SettingsAccountItemModel -> true
            oldItem is SettingsSectionItemModel && newItem is SettingsSectionItemModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SettingsListModel, newItem: SettingsListModel): Boolean {
        return when {
            oldItem is SettingsAccountItemModel && newItem is SettingsAccountItemModel -> oldItem == newItem
            oldItem is SettingsSectionItemModel && newItem is SettingsSectionItemModel -> oldItem.id == newItem.id
            else -> false
        }
    }
}