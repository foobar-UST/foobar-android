package com.foobarust.android.settings

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.ItemSettingsProfileBinding
import com.foobarust.android.databinding.ItemSettingsSectionBinding
import com.foobarust.android.settings.SettingsAdapter.SettingsAdapterListener
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.settings.SettingsViewHolder.SettingsAuthViewHolder
import com.foobarust.android.settings.SettingsViewHolder.SettingsSectionViewHolder
import com.foobarust.android.utils.*
import com.foobarust.domain.models.AuthProfile

class SettingsAdapter(
    private val listener: SettingsAdapterListener
) : ListAdapter<SettingsListModel, SettingsViewHolder>(SettingsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_settings_profile -> SettingsAuthViewHolder(
                ItemSettingsProfileBinding.inflate(inflater, parent, false),
                listener
            )

            R.layout.item_settings_section -> SettingsSectionViewHolder(
                ItemSettingsSectionBinding.inflate(inflater, parent, false),
                listener
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        when (holder) {
            is SettingsAuthViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SettingsProfileModel
                listener = this@SettingsAdapter.listener

                // Set user icon
                if (currentItem.authProfile?.photoUrl != null) {
                    avatarImageView.bindGlideUrl(currentItem.authProfile.photoUrl, centerCrop = true)
                } else {
                    avatarImageView.bindGlideSrc(R.drawable.ic_user, centerCrop = true)
                }

                // Set username text view
                with(usernameTextView) {
                    val spannable = if (currentItem.authProfile == null) {
                        buildSignedOutSpannable(context)
                    } else {
                        buildSignedInSpannable(context, currentItem.authProfile.username)
                    }

                    setText(spannable, TextView.BufferType.SPANNABLE)
                }

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
            is SettingsProfileModel -> R.layout.item_settings_profile
            is SettingsSectionModel -> R.layout.item_settings_section
        }
    }

    private fun buildSignedInSpannable(context: Context, username: String?): SpannableString {
        val extraText = context.getString(R.string.settings_account_edit_profile)

        return SpannableString(
            "$username\n$extraText"
        ).also {
            it.setSpan(
                TextAppearanceSpan(context, context.themeStyle(R.attr.textAppearanceBody2)),
                it.length - extraText.length,
                it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            it.setSpan(
                ForegroundColorSpan(context.themeColor(R.attr.colorPrimary)),
                it.length - extraText.length,
                it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun buildSignedOutSpannable(context: Context): SpannableString {
        val guestText = context.getString(R.string.settings_account_guest)
        val extraText = context.getString(R.string.settings_account_sign_in)

        return SpannableString(
            "$guestText\n$extraText"
        ).also {
            it.setSpan(
                TextAppearanceSpan(context, context.themeStyle(R.attr.textAppearanceBody2)),
                it.length - extraText.length,
                it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            it.setSpan(
                ForegroundColorSpan(context.getColorCompat(R.color.material_on_background_emphasis_medium)),
                it.length - extraText.length,
                it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    interface SettingsAdapterListener {
        fun onSettingsUserProfileClicked()
        fun onSettingsSectionClicked(sectionId: String)
    }
}

sealed class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SettingsAuthViewHolder(
        val binding: ItemSettingsProfileBinding,
        val listener: SettingsAdapterListener
    ) : SettingsViewHolder(binding.root)

    class SettingsSectionViewHolder(
        val binding: ItemSettingsSectionBinding,
        val listener: SettingsAdapterListener
    ) : SettingsViewHolder(binding.root)
}

sealed class SettingsListModel {
    data class SettingsProfileModel(
        val authProfile: AuthProfile?
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
            oldItem is SettingsProfileModel && newItem is SettingsProfileModel -> oldItem.authProfile == newItem.authProfile
            oldItem is SettingsSectionModel && newItem is SettingsSectionModel -> oldItem.id == newItem.id
            else -> false
        }
    }
}