package com.foobarust.android.sellerrating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.SellerRatingDetailInfoItemBinding
import com.foobarust.android.databinding.SellerRatingDetailRatingItemBinding
import com.foobarust.android.sellerrating.SellerRatingDetailListModel.*
import com.foobarust.android.sellerrating.SellerRatingDetailViewHolder.*
import com.foobarust.android.utils.bindGlideUrl
import com.foobarust.android.utils.round
import com.foobarust.domain.models.seller.SellerRatingCount
import com.foobarust.domain.models.seller.sum
import com.foobarust.domain.utils.format
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by kevin on 3/3/21
 */

class SellerRatingDetailAdapter(
    private val listener: SellerRatingDetailAdapterListener
) : PagingDataAdapter<SellerRatingDetailListModel, SellerRatingDetailViewHolder>(
    SellerRatingDetailListModelDiff
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellerRatingDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.seller_rating_detail_info_item -> SellerRatingDetailInfoViewHolder(
                SellerRatingDetailInfoItemBinding.inflate(inflater, parent, false)
            )
            R.layout.seller_rating_detail_rating_item -> SellerRatingDetailRatingViewHolder(
                SellerRatingDetailRatingItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")

        }
    }

    override fun onBindViewHolder(holder: SellerRatingDetailViewHolder, position: Int) {
       when (holder) {
           is SellerRatingDetailInfoViewHolder -> bindInfoItem(
               binding = holder.binding,
               infoItem = getItem(position) as? SellerRatingDetailInfoItem
           )
           is SellerRatingDetailRatingViewHolder -> bindRatingItem(
               binding = holder.binding,
               ratingItem = getItem(position) as? SellerRatingDetailRatingItem
           )
       }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerRatingDetailInfoItem -> R.layout.seller_rating_detail_info_item
            is SellerRatingDetailRatingItem -> R.layout.seller_rating_detail_rating_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    private fun bindInfoItem(
        binding: SellerRatingDetailInfoItemBinding,
        infoItem: SellerRatingDetailInfoItem?
    ) = binding.run {
        if (infoItem == null) return@run

        val context = root.context

        // Order rating
        orderRatingTextView.text = String.format("%.1f", infoItem.orderRating)
        orderRatingRatingBar.rating = infoItem.orderRating.round(1).toFloat()

        val totalRatingCount = infoItem.ratingCount.sum()

        ratingCountTextView.text = context.getString(
            R.string.seller_rating_detail_info_item_rating_count,
            totalRatingCount
        )

        // Delivery rating
        with(deliveryRatingTextView) {
            isVisible = infoItem.deliveryRating != null
            text = context.getString(
                R.string.seller_rating_detail_info_item_delivery_rating,
                infoItem.deliveryRating
            )
        }

        // Excellent
        setOrderRatingCount(
            progressIndicator = orderRating5StarIndicator,
            percentTextView = orderRating5StarPercentTextView,
            ratingCount = infoItem.ratingCount.excellent,
            totalRatingCount = totalRatingCount
        )

        // Very good
        setOrderRatingCount(
            progressIndicator = orderRating4StarIndicator,
            percentTextView = orderRating4StarPercentTextView,
            ratingCount = infoItem.ratingCount.veryGood,
            totalRatingCount = totalRatingCount
        )

        // Good
        setOrderRatingCount(
            progressIndicator = orderRating3StarIndicator,
            percentTextView = orderRating3StarPercentTextView,
            ratingCount = infoItem.ratingCount.good,
            totalRatingCount = totalRatingCount
        )

        // Fair
        setOrderRatingCount(
            progressIndicator = orderRating2StarIndicator,
            percentTextView = orderRating2StarPercentTextView,
            ratingCount = infoItem.ratingCount.fair,
            totalRatingCount = totalRatingCount
        )

        // Poor
        setOrderRatingCount(
            progressIndicator = orderRating1StarIndicator,
            percentTextView = orderRating1StarPercentTextView,
            ratingCount = infoItem.ratingCount.poor,
            totalRatingCount = totalRatingCount
        )

        // Sorting button
        with(ratingSubtitleSortButton) {
            text = "Sort By"
            setOnClickListener {
                listener.onSortRatingButtonClicked()
            }
        }

        executePendingBindings()
    }

    private fun setOrderRatingCount(
        progressIndicator: LinearProgressIndicator,
        percentTextView: TextView,
        ratingCount: Int,
        totalRatingCount: Int
    ) {
        if (totalRatingCount == 0) {
            progressIndicator.progress = 0
            percentTextView.text = "n.a."
        } else {
            val percent = (ratingCount.toDouble() / totalRatingCount * 100).roundToInt()
            progressIndicator.progress = percent
            percentTextView.text = percent.toString()
        }
    }

    private fun bindRatingItem(
        binding: SellerRatingDetailRatingItemBinding,
        ratingItem: SellerRatingDetailRatingItem?
    ) = binding.run {
        if (ratingItem == null) return@run

        ratingItemUserImageView.bindGlideUrl(
            imageUrl = ratingItem.userPhotoUrl,
            centerCrop = true,
            placeholder = R.drawable.ic_user
        )

        ratingItemUsernameTextView.text = ratingItem.username
        ratingItemRatingBar.rating = ratingItem.orderRating.toFloat()

        ratingItemCreatedAtTextView.text = ratingItem.createdAt.format("dd/MM/yyyy")

        executePendingBindings()
    }

    interface SellerRatingDetailAdapterListener {
        fun onSortRatingButtonClicked()
    }
}

sealed class SellerRatingDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerRatingDetailInfoViewHolder(
        val binding: SellerRatingDetailInfoItemBinding
    ) : SellerRatingDetailViewHolder(binding.root)

    class SellerRatingDetailRatingViewHolder(
        val binding: SellerRatingDetailRatingItemBinding
    ) : SellerRatingDetailViewHolder(binding.root)
}

sealed class SellerRatingDetailListModel {
    data class SellerRatingDetailInfoItem(
        val orderRating: Double,                // round to .1f
        val deliveryRating: Double?,            // round to percent int
        val ratingCount: SellerRatingCount
    ) : SellerRatingDetailListModel()

    data class SellerRatingDetailRatingItem(
        val ratingId: String,
        val username: String,
        val userPhotoUrl: String?,
        val orderRating: Double,
        val createdAt: Date
    ) : SellerRatingDetailListModel()
}

object SellerRatingDetailListModelDiff : DiffUtil.ItemCallback<SellerRatingDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SellerRatingDetailListModel,
        newItem: SellerRatingDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerRatingDetailInfoItem && newItem is SellerRatingDetailInfoItem ->
                true
            oldItem is SellerRatingDetailRatingItem && newItem is SellerRatingDetailRatingItem ->
                oldItem.ratingId == newItem.ratingId
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SellerRatingDetailListModel,
        newItem: SellerRatingDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerRatingDetailInfoItem && newItem is SellerRatingDetailInfoItem ->
                oldItem == newItem
            oldItem is SellerRatingDetailRatingItem && newItem is SellerRatingDetailRatingItem ->
                oldItem == newItem
            else -> false
        }
    }
}

