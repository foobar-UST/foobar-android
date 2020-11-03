package com.foobarust.android.sellerdetail

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.foobarust.android.databinding.DialogItemDetailBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.showShortToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by kevin on 10/12/20
 */

@AndroidEntryPoint
class SellerItemDetailDialog : BottomSheetDialogFragment(), SellerItemDetailAdapter.SellerItemDetailAdapterListener {

    private var binding: DialogItemDetailBinding by AutoClearedValue(this)
    private val viewModel: SellerItemDetailViewModel by viewModels()
    private val args: SellerItemDetailDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive itemId argument and start fetching ItemDetail
        viewModel.onFetchItemDetail(itemId = args.itemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogItemDetailBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerItemDetailDialog.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.onReceiveItemInfo(
            title = args.itemTitle,
            description = args.itemDescription,
            price = args.itemPrice.toDouble()
        )

        // Setup recycler view
        val itemDetailAdapter = SellerItemDetailAdapter(this)

        binding.recyclerView.run {
            adapter = itemDetailAdapter
            isNestedScrollingEnabled = true
        }

        viewModel.itemDetailModels.observe(viewLifecycleOwner) {
            itemDetailAdapter.submitList(it)
        }

        // Show toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Set initial fixed peek height
        val bottomSheetBehavior = (requireDialog() as BottomSheetDialog).behavior
        val screenHeight = getScreenHeightPixels()

        Log.d("SellerItemDetailDialog", "height: $screenHeight")

        bottomSheetBehavior.peekHeight = (getScreenHeightPixels() * 0.4).toInt()

        // Finished adding to cart and exit the dialog
        viewModel.submittedToCart.observe(viewLifecycleOwner) {
            dismiss()
        }

        return binding.root
    }

    override fun onChoiceSelected(choiceId: String) {
        showShortToast("choiceId: $choiceId")
    }

    override fun onExtraItemChecked(view: View, isChecked: Boolean, extraItemId: String) {
        showShortToast("extraItemId: $extraItemId")
    }

    override fun onNotesChanged(notes: String) {

    }

    override fun onSubmitClicked() {
        viewModel.onSubmitToCart()
    }

    private fun getScreenHeightPixels(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

            displayMetrics.heightPixels
        }
    }
}