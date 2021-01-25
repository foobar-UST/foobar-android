package com.foobarust.android.sellerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerItemDetailBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.*
import com.foobarust.android.utils.AppBarStateChangedListener.*
import com.foobarust.domain.models.seller.SellerItemBasic
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailFragment : FullScreenDialogFragment(), SellerItemDetailAdapter.SellerItemDetailAdapterListener {

    private var binding: FragmentSellerItemDetailBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val itemDetailViewModel: SellerItemDetailViewModel by viewModels()
    private val args: SellerItemDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemDetailViewModel.onFetchItemDetail(property = args.property)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerItemDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemDetailFragment.itemDetailViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val itemDetailAdapter = SellerItemDetailAdapter(this)
        binding.recyclerView.run {
            adapter = itemDetailAdapter
            setHasFixedSize(true)
        }

        itemDetailViewModel.itemDetailListModels.observe(viewLifecycleOwner) {
            itemDetailAdapter.submitList(it)
        }

        // Remove listener on CollapsingToolbarLayout, so that toolbar top padding can work properly
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbarLayout, null)

        // Show toolbar title only when collapsed
        viewLifecycleOwner.lifecycleScope.launch {
            binding.appBarLayout.doOnOffsetChanged().collect {
                itemDetailViewModel.onToolbarCollapsed(
                    isCollapsed = it == State.COLLAPSED
                )
            }
        }

        // Setup amount widget
        binding.amountIncrementButton.setOnClickListener {
            itemDetailViewModel.onAmountIncrement()
        }

        binding.amountDecrementButton.setOnClickListener {
            itemDetailViewModel.onAmountDecrement()
        }

        // Toolbar navigate back
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Setup submit button
        binding.submitButton.setOnClickListener {
            itemDetailViewModel.onSubmitItem(
                currentUserCart = mainViewModel.getCurrentUserCart()
            )
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            itemDetailViewModel.onFetchItemDetail(args.property)
        }

        // Dismiss dialog when finish submitting
        itemDetailViewModel.dismissDialog.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        // Prevent dismissing dialog when submitting to cart
        itemDetailViewModel.isSubmitting.observe(viewLifecycleOwner) { submitting ->
            requireDialog().setCancelable(!submitting)
        }

        // Show toast
        itemDetailViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Diff seller
        itemDetailViewModel.showDiffSellerDialog.observe(viewLifecycleOwner) {
            showDiffSellerDialog()
        }

        return binding.root
    }

    override fun onMoreItemClicked(itemBasic: SellerItemBasic) {
        findNavController(R.id.sellerItemDetailFragment)?.navigate(
            SellerItemDetailFragmentDirections.actionSellerItemDetailFragmentSelf(
                SellerItemDetailProperty(
                    sellerId = args.property.sellerId,
                    itemId = itemBasic.id
                )
            )
        )
    }

    override fun onMoreItemCheckedChange(itemBasic: SellerItemBasic, isChecked: Boolean) {
        itemDetailViewModel.onExtraItemCheckedChange(itemBasic, isChecked)
    }

    private fun showDiffSellerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.seller_item_detail_diff_seller_title))
            .setMessage(getString(R.string.seller_item_detail_diff_seller_message))
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}