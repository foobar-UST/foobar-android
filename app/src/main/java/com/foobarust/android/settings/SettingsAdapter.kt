package com.foobarust.android.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SettingsProfileItemBinding
import com.foobarust.android.databinding.SettingsSectionItemBinding
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.settings.SettingsViewHolder.SettingsProfileViewHolder
import com.foobarust.android.settings.SettingsViewHolder.SettingsSectionViewHolder
import com.foobarust.android.utils.bindGlideSrc
import com.foobarust.android.utils.bindGlideUrl
import com.foobarust.android.utils.getColorCompat
import com.foobarust.android.utils.themeColor

class SettingsAdapter(
    private val listener: SettingsAdapterListener
) : ListAdapter<SettingsListModel, SettingsViewHolder>(SettingsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.settings_profile_item -> SettingsProfileViewHolder(
                SettingsProfileItemBinding.inflate(inflater, parent, false)
            )

            R.layout.settings_section_item -> SettingsSectionViewHolder(
                SettingsSectionItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        when (holder) {
            is SettingsProfileViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SettingsProfileModel

                profileModel = currentItem
                listener = this@SettingsAdapter.listener

                // Set user avatar if the user is signed in or he has uploaded his photo,
                // else set it to placeholder drawable
                if (currentItem.settingsProfile.hasPhoto()) {
                    avatarImageView.bindGlideUrl(
                        imageUrl = currentItem.settingsProfile.photoUrl,
                        centerCrop = true
                    )
                } else {
                    avatarImageView.bindGlideSrc(
                        drawableRes = R.drawable.ic_user,
                        centerCrop = true
                    )
                }

                // Set description text color
                setDescriptionTextColor(
                    textView = descriptionTextView,
                    isSignedIn = currentItem.settingsProfile.isSignedIn()
                )

                executePendingBindings()
            }

            is SettingsSectionViewHolder ->  holder.binding.run {
                section = getItem(position) as SettingsSectionModel
                listener = this@SettingsAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingsProfileModel -> R.layout.settings_profile_item
            is SettingsSectionModel -> R.layout.settings_section_item
        }
    }

    private fun setDescriptionTextColor(textView: TextView, isSignedIn: Boolean) {
        val context = textView.context

        if (isSignedIn) {
            textView.setTextColor(context.themeColor(R.attr.colorPrimary))
        } else {
            textView.setTextColor(context.getColorCompat(R.color.material_on_background_emphasis_medium))
        }
    }

    interface SettingsAdapterListener {
        fun onSettingsUserProfileClicked()
        fun onSettingsSectionClicked(sectionId: String)
    }
}

sealed class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SettingsProfileViewHolder(
        val binding: SettingsProfileItemBinding
    ) : SettingsViewHolder(binding.root)

    class SettingsSectionViewHolder(
        val binding: SettingsSectionItemBinding
    ) : SettingsViewHolder(binding.root)
}

sealed class SettingsListModel {
    data class SettingsProfileModel(
        val settingsProfile: SettingsProfile
    ) : SettingsListModel()

    data class SettingsSectionModel(
        val id: String,
        @DrawableRes val icon: Int,
        val title: String
    ) : SettingsListModel()
}

object SettingsListModelDiff : DiffUtil.ItemCallback<SettingsListModel>() {
    override fun areItemsTheSame(oldItem: SettingsListModel, newItem: SettingsListModel): Boolean {
        return when {
            oldItem is SettingsProfileModel && newItem is SettingsProfileModel -> true
            oldItem is SettingsSectionModel && newItem is SettingsSectionModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SettingsListModel, newItem: SettingsListModel): Boolean {
        return when {
            oldItem is SettingsProfileModel && newItem is SettingsProfileModel -> oldItem.settingsProfile == newItem.settingsProfile
            oldItem is SettingsSectionModel && newItem is SettingsSectionModel -> oldItem.id == newItem.id
            else -> false
        }
    }
}