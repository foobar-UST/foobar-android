package com.foobarust.android.sellersection

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.sellersection.ParticipantsListModel.*
import com.foobarust.android.sellersection.RelatedSectionsListModel.*
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.android.sellersection.SellerSectionDetailViewHolder.*
import com.foobarust.android.utils.ScrollStatesManager
import com.foobarust.domain.models.user.UserPublic
import com.foobarust.domain.utils.format
import com.foobarust.domain.utils.getTimeBy12Hour
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by kevin on 12/22/20
 */

class SellerSectionDetailAdapter(
    private val sellerSectionDetailFragment: SellerSectionDetailFragment
) : ListAdapter<SellerSectionDetailListModel, SellerSectionDetailViewHolder>(
    SellerSectionDetailListModelDiff
) {

    private val scrollStatesManager = ScrollStatesManager()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerSectionDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_section_detail_participants_item -> SellerSectionDetailParticipantsItemViewHolder(
                SellerSectionDetailParticipantsItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_counter_item -> SellerSectionDetailCounterItemViewHolder(
                SellerSectionDetailCounterItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_section_info_item -> SellerSectionDetailSectionInfoItemViewHolder(
                SellerSectionDetailSectionInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_related_item -> SellerSectionDetailRelatedItemViewHolder(
                SellerSectionDetailRelatedItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerSectionDetailViewHolder, position: Int) {
        when (holder) {
            is SellerSectionDetailParticipantsItemViewHolder -> bindParticipantsItem(
                binding = holder.binding,
                participantsItemModel = getItem(position) as SellerSectionDetailParticipantsItemModel,
                layoutPosition = holder.layoutPosition
            )
            is SellerSectionDetailCounterItemViewHolder -> bindCounterItem(
                binding = holder.binding,
                counterItemModel = getItem(position) as SellerSectionDetailCounterItemModel
            )
            is SellerSectionDetailSectionInfoItemViewHolder -> bindSectionInfo(
                binding = holder.binding,
                sectionInfoItemModel = getItem(position) as SellerSectionDetailSectionInfoItemModel
            )
            is SellerSectionDetailRelatedItemViewHolder -> bindRelatedItem(
                binding = holder.binding,
                relatedItemModel = getItem(position) as SellerSectionDetailRelatedItemModel,
                layoutPosition = holder.layoutPosition
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerSectionDetailParticipantsItemModel -> R.layout.seller_section_detail_participants_item
            is SellerSectionDetailCounterItemModel -> R.layout.seller_section_detail_counter_item
            is SellerSectionDetailSectionInfoItemModel -> R.layout.seller_section_detail_section_info_item
            is SellerSectionDetailRelatedItemModel -> R.layout.seller_section_detail_related_item
        }
    }

    override fun onViewRecycled(holder: SellerSectionDetailViewHolder) {
        super.onViewRecycled(holder)

        if (holder is SellerSectionDetailParticipantsItemViewHolder) {
            scrollStatesManager.saveScrollState(
                layoutPosition = holder.layoutPosition,
                recyclerView = holder.binding.participantsRecyclerView
            )
        } else if (holder is SellerSectionDetailRelatedItemViewHolder) {
            scrollStatesManager.saveScrollState(
                layoutPosition = holder.layoutPosition,
                recyclerView = holder.binding.sectionsRecyclerView
            )
        }
    }

    private fun getCounterRemainTime(timeMills: Long): CounterRemainTime {
        return CounterRemainTime(
            hours = (
                TimeUnit.MILLISECONDS.toHours(timeMills) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeMills))
            ).toInt(),
            minutes = (
                TimeUnit.MILLISECONDS.toMinutes(timeMills) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeMills))
            ).toInt(),
            seconds = (
                TimeUnit.MILLISECONDS.toSeconds(timeMills) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMills))
            ).toInt()
        )
    }

    private fun bindParticipantsItem(
        binding: SellerSectionDetailParticipantsItemBinding,
        participantsItemModel: SellerSectionDetailParticipantsItemModel,
        layoutPosition: Int
    ) = binding.run {
        val participantsAdapter = ParticipantsAdapter(
            sectionId = participantsItemModel.sectionId,
            listener = sellerSectionDetailFragment
        ).apply {
            submitList(participantsItemModel.usersPublics.map {
                ParticipantsItemModel(userPublic = it)
            })
        }

        participantsRecyclerView.run {
            adapter = participantsAdapter
            scrollStatesManager.restoreScrollState(
                layoutPosition = layoutPosition,
                recyclerView = this
            )
            setHasFixedSize(true)
        }

        participantsSubtitleTextView.text = if (participantsItemModel.usersCount > 0) {
            root.context.getString(
                R.string.seller_section_detail_users_subtitle,
                participantsItemModel.usersCount,
                participantsItemModel.maxUsers
            )
        } else {
            root.context.getString(R.string.seller_section_detail_users_subtitle_empty)
        }
    }

    private fun bindCounterItem(
        binding: SellerSectionDetailCounterItemBinding,
        counterItemModel: SellerSectionDetailCounterItemModel
    ) = binding.run {
        if (counterItemModel.isRecentSection) {
            counterSubtitleTextView.text = root.context.getString(
                R.string.seller_section_detail_counter_on_subtitle
            )
            // Setup countdown timer for recent section
            sellerSectionDetailFragment.viewLifecycleOwner.lifecycleScope.launch {
                val timeMills = counterItemModel.cutoffTime.time - Date().time
                val timer = object : CountDownTimer(timeMills, 1_000L) {
                    override fun onTick(millisUntilFinished: Long) {
                        val remainTime = getCounterRemainTime(millisUntilFinished)
                        counterValueTextView.text = String.format(
                            "%02d : %02d : %02d",
                            remainTime.hours,
                            remainTime.minutes,
                            remainTime.seconds
                        )
                    }
                    override fun onFinish() { cancel() }
                }
                timer.start()
            }
        } else {
            counterSubtitleTextView.text = root.context.getString(
                R.string.seller_section_detail_counter_off_subtitle
            )
            counterValueTextView.text = counterItemModel.cutoffTime.format("yyyy-MM-dd")
        }
    }

    private fun bindSectionInfo(
        binding: SellerSectionDetailSectionInfoItemBinding,
        sectionInfoItemModel: SellerSectionDetailSectionInfoItemModel
    ) = binding.run {
        descriptionTextView.text = sectionInfoItemModel.description
        deliveryDateTextView.text = sectionInfoItemModel.deliveryTime.format("yyyy-MM-dd")
        cutoffTimeTextView.text = sectionInfoItemModel.cutoffTime.getTimeBy12Hour()
        deliveryTimeTextView.text = sectionInfoItemModel.deliveryTime.getTimeBy12Hour()
        pickUpLocationTextView.text = sectionInfoItemModel.deliveryLocation
    }

    private fun bindRelatedItem(
        binding: SellerSectionDetailRelatedItemBinding,
        relatedItemModel: SellerSectionDetailRelatedItemModel,
        layoutPosition: Int
    ) = binding.run {
        // Setup more sections recycler view
        val adapter = RelatedSectionsAdapter(
            sellerId = relatedItemModel.sellerId,
            listener = this@SellerSectionDetailAdapter.sellerSectionDetailFragment
        ).apply {
            submitList(relatedItemModel.itemModels)
        }

        sectionsRecyclerView.run {
            this.adapter = adapter
            setHasFixedSize(true)
            scrollStatesManager.restoreScrollState(
                layoutPosition = layoutPosition,
                recyclerView = this
            )
        }
    }

    private data class CounterRemainTime(val hours: Int, val minutes: Int, val seconds: Int)

    interface SellerSectionDetailAdapterListener {
        fun onSellerInfoItemClicked(sellerId: String)
    }
}

sealed class SellerSectionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class SellerSectionDetailParticipantsItemViewHolder(
        val binding: SellerSectionDetailParticipantsItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailCounterItemViewHolder(
        val binding: SellerSectionDetailCounterItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailSectionInfoItemViewHolder(
        val binding: SellerSectionDetailSectionInfoItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailRelatedItemViewHolder(
        val binding: SellerSectionDetailRelatedItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)
}

sealed class SellerSectionDetailListModel {
    data class SellerSectionDetailParticipantsItemModel(
        val sectionId: String,
        val usersCount: Int,
        val maxUsers: Int,
        val usersPublics: List<UserPublic>
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailCounterItemModel(
        val cutoffTime: Date,
        val isRecentSection: Boolean
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailSectionInfoItemModel(
        val description: String,
        val cutoffTime: Date,
        val deliveryTime: Date,
        val deliveryLocation: String
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailRelatedItemModel(
        val sellerId: String,
        val itemModels: List<RelatedSectionsItemModel>
    ) : SellerSectionDetailListModel()
}

object SellerSectionDetailListModelDiff : DiffUtil.ItemCallback<SellerSectionDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerSectionDetailListModel,
        newItem: SellerSectionDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionDetailParticipantsItemModel &&
                newItem is SellerSectionDetailParticipantsItemModel ||
            oldItem is SellerSectionDetailCounterItemModel &&
                newItem is SellerSectionDetailCounterItemModel ||
            oldItem is SellerSectionDetailSectionInfoItemModel &&
                newItem is SellerSectionDetailSectionInfoItemModel ||
            oldItem is SellerSectionDetailRelatedItemModel &&
                newItem is SellerSectionDetailRelatedItemModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerSectionDetailListModel,
        newItem: SellerSectionDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionDetailParticipantsItemModel &&
                newItem is SellerSectionDetailParticipantsItemModel ||
            oldItem is SellerSectionDetailCounterItemModel &&
                newItem is SellerSectionDetailCounterItemModel ||
            oldItem is SellerSectionDetailSectionInfoItemModel &&
                newItem is SellerSectionDetailSectionInfoItemModel ||
            oldItem is SellerSectionDetailRelatedItemModel &&
                newItem is SellerSectionDetailRelatedItemModel ->
                oldItem == newItem
            else -> false
        }
    }
}