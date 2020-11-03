package com.foobarust.android.sellerdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.android.R
import com.foobarust.android.databinding.*
import com.foobarust.android.sellerdetail.SellerItemDetailListModel.*
import com.foobarust.android.sellerdetail.SellerItemDetailViewHolder.*
import com.foobarust.domain.models.SellerItemChoice
import com.foobarust.domain.models.SellerItemExtraItem
import com.google.android.material.radiobutton.MaterialRadioButton

/**
 * Created by kevin on 10/12/20
 */

class SellerItemDetailAdapter(
    private val listener: SellerItemDetailAdapterListener
) : ListAdapter<SellerItemDetailListModel, SellerItemDetailViewHolder>(SellerItemDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerItemDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.seller_item_detail_info_item -> SellerItemDetailInfoViewHolder(
                SellerItemDetailInfoItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_subtitle_item -> SellerItemDetailSubtitleViewHolder(
                SellerItemDetailSubtitleItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_choices_item -> SellerItemDetailChoicesViewHolder(
                SellerItemDetailChoicesItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_extra_item -> SellerItemDetailExtraItemViewHolder(
                SellerItemDetailExtraItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_notes_item -> SellerItemDetailNotesViewHolder(
                SellerItemDetailNotesItemBinding.inflate(inflater, parent, false)
            )

            R.layout.seller_item_detail_submit_item -> SellerItemDetailSubmitViewHolder(
                SellerItemDetailSubmitItemBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SellerItemDetailViewHolder, position: Int) {
        when (holder) {
            is SellerItemDetailInfoViewHolder -> holder.binding.run {
                infoModel = getItem(position) as SellerItemDetailInfoModel
                executePendingBindings()
            }

            is SellerItemDetailSubtitleViewHolder -> holder.binding.run {
                subtitleModel = getItem(position) as SellerItemDetailSubtitleModel
                executePendingBindings()
            }

            is SellerItemDetailChoicesViewHolder -> holder.binding.run {
                // Add choice radio buttons to RadioGroup
                val currentItem = getItem(position) as SellerItemDetailChoicesModel

                currentItem.choices.forEach { choice ->
                    MaterialRadioButton(
                        choicesRadioGroup.context,
                        null,
                        R.attr.radioButtonStyle
                    ).apply {
                        // Set button title
                        text = context.getString(
                            R.string.item_detail_choice_item_title,
                            choice.title,
                            choice.extraPrice
                        )
                    }.also {
                        // Add button to radio group
                        choicesRadioGroup.addView(it)
                    }
                }

                choicesRadioGroup.run {
                    // Checked first choice item
                    check(getChildAt(0).id)

                    // Register radio group listener
                    setOnCheckedChangeListener { radioGroup, checkedId ->
                        val buttonIndex = radioGroup.indexOfChild(
                            radioGroup.findViewById<RadioButton>(checkedId)
                        )

                        listener.onChoiceSelected(currentItem.choices[buttonIndex].id)
                    }
                }

                executePendingBindings()
            }

            is SellerItemDetailExtraItemViewHolder -> holder.binding.run {
                extraItemModel = getItem(position) as SellerItemDetailExtraItemModel
                listener = this@SellerItemDetailAdapter.listener
                executePendingBindings()
            }

            is SellerItemDetailNotesViewHolder -> holder.binding.run {
                notesEditText.doOnTextChanged { text, _, _, _ ->
                    listener.onNotesChanged(text.toString())
                }

                executePendingBindings()
            }

            is SellerItemDetailSubmitViewHolder -> holder.binding.run {
                submitModel = getItem(position) as SellerItemDetailSubmitModel
                listener = this@SellerItemDetailAdapter.listener
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SellerItemDetailInfoModel -> R.layout.seller_item_detail_info_item
            is SellerItemDetailSubtitleModel -> R.layout.seller_item_detail_subtitle_item
            is SellerItemDetailChoicesModel -> R.layout.seller_item_detail_choices_item
            is SellerItemDetailExtraItemModel -> R.layout.seller_item_detail_extra_item
            is SellerItemDetailNotesModel -> R.layout.seller_item_detail_notes_item
            is SellerItemDetailSubmitModel -> R.layout.seller_item_detail_submit_item
        }
    }

    interface SellerItemDetailAdapterListener {
        fun onChoiceSelected(choiceId: String)
        fun onExtraItemChecked(view: View, isChecked: Boolean, extraItemId: String)
        fun onNotesChanged(notes: String)
        fun onSubmitClicked()
    }
}

sealed class SellerItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SellerItemDetailInfoViewHolder(
        val binding: SellerItemDetailInfoItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailSubtitleViewHolder(
        val binding: SellerItemDetailSubtitleItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailChoicesViewHolder(
        val binding: SellerItemDetailChoicesItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailExtraItemViewHolder(
        val binding: SellerItemDetailExtraItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailNotesViewHolder(
        val binding: SellerItemDetailNotesItemBinding
    ) : SellerItemDetailViewHolder(binding.root)

    class SellerItemDetailSubmitViewHolder(
        val binding: SellerItemDetailSubmitItemBinding
    ) : SellerItemDetailViewHolder(binding.root)
}

sealed class SellerItemDetailListModel {
    data class SellerItemDetailInfoModel(
        val itemTitle: String,
        val itemDescription: String?,
        val itemPrice: Double,
        val itemAccumPrice: Double? = null
    ) : SellerItemDetailListModel()

    data class SellerItemDetailSubtitleModel(
        val subtitle: String
    ) : SellerItemDetailListModel()

    data class SellerItemDetailChoicesModel(
        val choices: List<SellerItemChoice>
    ) : SellerItemDetailListModel()

    data class SellerItemDetailExtraItemModel(
        val extraItem: SellerItemExtraItem
    ) : SellerItemDetailListModel()

    object SellerItemDetailNotesModel : SellerItemDetailListModel()

    data class SellerItemDetailSubmitModel(
        val isAddingToCart: Boolean = false
    ) : SellerItemDetailListModel()
}

object SellerItemDetailListModelDiff : DiffUtil.ItemCallback<SellerItemDetailListModel>() {
    override fun areItemsTheSame(oldItem: SellerItemDetailListModel, newItem: SellerItemDetailListModel): Boolean {
       return when {
           oldItem is SellerItemDetailInfoModel && newItem is SellerItemDetailInfoModel -> true
           oldItem is SellerItemDetailSubtitleModel && newItem is SellerItemDetailSubtitleModel ->
               oldItem.subtitle == newItem.subtitle
           oldItem is SellerItemDetailChoicesModel && newItem is SellerItemDetailChoicesModel -> true
           oldItem is SellerItemDetailExtraItemModel && newItem is SellerItemDetailExtraItemModel ->
               oldItem.extraItem.id == newItem.extraItem.id
           oldItem is SellerItemDetailNotesModel && newItem is SellerItemDetailNotesModel -> true
           oldItem is SellerItemDetailSubmitModel && newItem is SellerItemDetailSubmitModel -> true
           else -> false
       }
    }

    override fun areContentsTheSame(oldItem: SellerItemDetailListModel, newItem: SellerItemDetailListModel
    ): Boolean {
        return when {
            oldItem is SellerItemDetailInfoModel && newItem is SellerItemDetailInfoModel -> oldItem == newItem
            oldItem is SellerItemDetailSubtitleModel && newItem is SellerItemDetailSubtitleModel ->
                oldItem.subtitle == newItem.subtitle
            oldItem is SellerItemDetailChoicesModel && newItem is SellerItemDetailChoicesModel ->
                oldItem.choices == newItem.choices
            oldItem is SellerItemDetailExtraItemModel && newItem is SellerItemDetailExtraItemModel ->
                oldItem.extraItem == newItem.extraItem
            oldItem is SellerItemDetailNotesModel && newItem is SellerItemDetailNotesModel -> true
            oldItem is SellerItemDetailSubmitModel && newItem is SellerItemDetailSubmitModel ->
                oldItem.isAddingToCart == newItem.isAddingToCart
            else -> false
        }
    }
}