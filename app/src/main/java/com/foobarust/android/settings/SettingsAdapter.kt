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
import com.foobarust.android.databinding.SettingsProfileItemBinding
import com.foobarust.android.databinding.SettingsSectionItemBinding
import com.foobarust.android.settings.SettingsAdapter.SettingsAdapterListener
import com.foobarust.android.settings.SettingsListModel.SettingsProfileModel
import com.foobarust.android.settings.SettingsListModel.SettingsSectionModel
import com.foobarust.android.settings.SettingsViewHolder.SettingsProfileViewHolder
import com.foobarust.android.settings.SettingsViewHolder.SettingsSectionViewHolder
import com.foobarust.android.utils.*
import com.foobarust.domain.models.UserDetail

class SettingsAdapter(
    private val listener: SettingsAdapterListener
) : ListAdapter<SettingsListModel, SettingsViewHolder>(SettingsListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.settings_profile_item -> SettingsProfileViewHolder(
                SettingsProfileItemBinding.inflate(inflater, parent, false),
                listener
            )

            R.layout.settings_section_item -> SettingsSectionViewHolder(
                SettingsSectionItemBinding.inflate(inflater, parent, false),
                listener
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        when (holder) {
            is SettingsProfileViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SettingsProfileModel
                listener = this@SettingsAdapter.listener

                // Set user icon
                if (currentItem.userDetail?.photoUrl != null) {
                    avatarImageView.bindGlideUrl(currentItem.userDetail.photoUrl, centerCrop = true)
                } else {
                    avatarImageView.bindGlideSrc(R.drawable.ic_user, centerCrop = true)
                }

                // Set username text view
                with(usernameTextView) {
                    val spannable = if (currentItem.userDetail == null) {
                        buildSignedOutSpannable(context)
                    } else {
                        buildSignedInSpannable(context, currentItem.userDetail.username)
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
            is SettingsProfileModel -> R.layout.settings_profile_item
            is SettingsSectionModel -> R.layout.settings_section_item
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
                // TODO: fix wrong color in older android version
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
                // TODO: fix wrong color in older android version
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
    class SettingsProfileViewHolder(
        val binding: SettingsProfileItemBinding,
        val listener: SettingsAdapterListener
    ) : SettingsViewHolder(binding.root)

    class SettingsSectionViewHolder(
        val binding: SettingsSectionItemBinding,
        val listener: SettingsAdapterListener
    ) : SettingsViewHolder(binding.root)
}

sealed class SettingsListModel {
    data class SettingsProfileModel(
        val userDetail: UserDetail?
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
            oldItem is SettingsProfileModel && newItem is SettingsProfileModel -> oldItem.userDetail == newItem.userDetail
            oldItem is SettingsSectionModel && newItem is SettingsSectionModel -> oldItem.id == newItem.id
            else -> false
        }
    }
}