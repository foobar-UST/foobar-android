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
import com.foobarust.android.sellersection.MoreSectionsListModel.*
import com.foobarust.android.sellersection.ParticipantsListModel.*
import com.foobarust.android.sellersection.SellerSectionDetailListModel.*
import com.foobarust.android.sellersection.SellerSectionDetailViewHolder.*
import com.foobarust.domain.models.common.Geolocation
import com.foobarust.domain.models.user.UserPublic
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
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
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerSectionDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_section_detail_users_item -> SellerSectionDetailUsersItemViewHolder(
                SellerSectionDetailUsersItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_counter_item -> SellerSectionDetailCounterItemViewHolder(
                SellerSectionDetailCounterItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_section_info_item -> SellerSectionDetailSectionInfoItemViewHolder(
                SellerSectionDetailSectionInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_shipping_info_item -> SellerSectionDetailShippingInfoItemViewHolder(
                SellerSectionDetailShippingInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_seller_info_item -> SellerSectionDetailSellerInfoItemViewHolder(
                SellerSectionDetailSellerInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_section_detail_more_sections_item -> SellerSectionDetailMoreSectionsItemViewHolder(
                SellerSectionDetailMoreSectionsItemBinding.inflate(inflater, parent, false)
            )
            R.layout.subtitle_item -> SellerSectionDetailSubtitleItemViewHolder(
                SubtitleItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerSectionDetailViewHolder, position: Int) {
        when (holder) {
            is SellerSectionDetailUsersItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SellerSectionDetailUsersItemModel
                usersItem = currentItem

                // Setup users recycler view
                val participantsAdapter = ParticipantsAdapter(
                    sectionId = currentItem.sectionId,
                    listener = sellerSectionDetailFragment
                ).apply {
                    submitList(currentItem.usersPublics.map {
                        ParticipantsUserItem(userPublic = it)
                    })
                }

                usersRecyclerView.run {
                    adapter = participantsAdapter
                    setHasFixedSize(true)
                }

                executePendingBindings()
            }

            is SellerSectionDetailCounterItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SellerSectionDetailCounterItemModel

                // Setup countdown timer
                sellerSectionDetailFragment.viewLifecycleOwner.lifecycleScope.launch {
                    val timeMills = currentItem.cutoffTime.time - Date().time
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

                executePendingBindings()
            }

            is SellerSectionDetailSectionInfoItemViewHolder -> holder.binding.run {
                orderInfoItem = getItem(position) as SellerSectionDetailSectionInfoItemModel
                executePendingBindings()
            }

            is SellerSectionDetailShippingInfoItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SellerSectionDetailShippingInfoItemModel
                shippingInfoItem = currentItem

                // Setup map view
                sellerSectionDetailFragment.viewLifecycleOwner.lifecycleScope.launch {
                    addressMapView.onCreate(null)
                    addressMapView.awaitMap().run {
                        val latLng = LatLng(
                            currentItem.geolocation.latitude,
                            currentItem.geolocation.longitude
                        )
                        addMarker { position(latLng) }
                        //moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }

                executePendingBindings()
            }

            is SellerSectionDetailSellerInfoItemViewHolder -> holder.binding.run {
                sellerInfoItem = getItem(position) as SellerSectionDetailSellerInfoItemModel
                listener = this@SellerSectionDetailAdapter.sellerSectionDetailFragment
                executePendingBindings()
            }

            is SellerSectionDetailMoreSectionsItemViewHolder -> holder.binding.run {
                val currentItem = getItem(position) as SellerSectionDetailMoreSectionsItemModel

                // Setup more sections recycler view
                val moreSectionsAdapter = MoreSectionsAdapter(
                    sellerId = currentItem.sellerId,
                    listener = this@SellerSectionDetailAdapter.sellerSectionDetailFragment
                ).apply { submitList(currentItem.sectionItems) }

                sectionsRecyclerView.run {
                    adapter = moreSectionsAdapter
                    setHasFixedSize(true)
                }

                executePendingBindings()
            }

            is SellerSectionDetailSubtitleItemViewHolder -> holder.binding.run {
                subtitle = (getItem(position) as SellerSectionDetailSubtitleItemModel).subtitle
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerSectionDetailUsersItemModel -> R.layout.seller_section_detail_users_item
            is SellerSectionDetailCounterItemModel -> R.layout.seller_section_detail_counter_item
            is SellerSectionDetailSectionInfoItemModel -> R.layout.seller_section_detail_section_info_item
            is SellerSectionDetailShippingInfoItemModel -> R.layout.seller_section_detail_shipping_info_item
            is SellerSectionDetailSellerInfoItemModel -> R.layout.seller_section_detail_seller_info_item
            is SellerSectionDetailMoreSectionsItemModel -> R.layout.seller_section_detail_more_sections_item
            is SellerSectionDetailSubtitleItemModel -> R.layout.subtitle_item
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

    interface SellerSectionDetailAdapterListener {
        fun onSellerInfoItemClicked(sellerId: String)
    }

    private data class CounterRemainTime(
        val hours: Int,
        val minutes: Int,
        val seconds: Int
    )
}

sealed class SellerSectionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    data class SellerSectionDetailUsersItemViewHolder(
        val binding: SellerSectionDetailUsersItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailCounterItemViewHolder(
        val binding: SellerSectionDetailCounterItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailSectionInfoItemViewHolder(
        val binding: SellerSectionDetailSectionInfoItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailShippingInfoItemViewHolder(
        val binding: SellerSectionDetailShippingInfoItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailSellerInfoItemViewHolder(
        val binding: SellerSectionDetailSellerInfoItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailMoreSectionsItemViewHolder(
        val binding: SellerSectionDetailMoreSectionsItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)

    data class SellerSectionDetailSubtitleItemViewHolder(
        val binding: SubtitleItemBinding
    ) : SellerSectionDetailViewHolder(binding.root)
}

sealed class SellerSectionDetailListModel {
    data class SellerSectionDetailUsersItemModel(
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
        val cutoffTime: String,
        val deliveryDate: String,
        val deliveryTime: String
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailShippingInfoItemModel(
        val address: String,
        val geolocation: Geolocation
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailSellerInfoItemModel(
        val sellerId: String,
        val sellerName: String,
        val sellerRating: Double,
        val sellerImageUrl: String?
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailMoreSectionsItemModel(
        val sellerId: String,
        val sectionItems: List<MoreSectionsSectionItem>
    ) : SellerSectionDetailListModel()

    data class SellerSectionDetailSubtitleItemModel(
        val subtitle: String
    ) : SellerSectionDetailListModel()
}

object SellerSectionDetailListModelDiff : DiffUtil.ItemCallback<SellerSectionDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerSectionDetailListModel,
        newItem: SellerSectionDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionDetailUsersItemModel &&
                newItem is SellerSectionDetailUsersItemModel ||
            oldItem is SellerSectionDetailCounterItemModel &&
                newItem is SellerSectionDetailCounterItemModel ||
            oldItem is SellerSectionDetailSectionInfoItemModel &&
                newItem is SellerSectionDetailSectionInfoItemModel ||
            oldItem is SellerSectionDetailShippingInfoItemModel &&
                newItem is SellerSectionDetailShippingInfoItemModel ||
            oldItem is SellerSectionDetailSellerInfoItemModel &&
                newItem is SellerSectionDetailSellerInfoItemModel ||
            oldItem is SellerSectionDetailMoreSectionsItemModel &&
                newItem is SellerSectionDetailMoreSectionsItemModel||
            oldItem is SellerSectionDetailSubtitleItemModel &&
                newItem is SellerSectionDetailSubtitleItemModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerSectionDetailListModel,
        newItem: SellerSectionDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerSectionDetailUsersItemModel &&
                newItem is SellerSectionDetailUsersItemModel ||
            oldItem is SellerSectionDetailCounterItemModel &&
                newItem is SellerSectionDetailCounterItemModel ||
            oldItem is SellerSectionDetailSectionInfoItemModel &&
                newItem is SellerSectionDetailSectionInfoItemModel ||
            oldItem is SellerSectionDetailShippingInfoItemModel &&
                newItem is SellerSectionDetailShippingInfoItemModel ||
            oldItem is SellerSectionDetailSellerInfoItemModel &&
                newItem is SellerSectionDetailSellerInfoItemModel ||
            oldItem is SellerSectionDetailMoreSectionsItemModel &&
                newItem is SellerSectionDetailMoreSectionsItemModel||
            oldItem is SellerSectionDetailSubtitleItemModel &&
                newItem is SellerSectionDetailSubtitleItemModel -> oldItem == newItem
            else -> false
        }
    }
}