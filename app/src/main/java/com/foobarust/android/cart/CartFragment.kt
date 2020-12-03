package com.foobarust.android.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentCartBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.models.user.UserCartItem
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 10/29/20
 */

@AndroidEntryPoint
class CartFragment : DialogFragment(), CartAdapter.CartAdapterListener {

    private var binding: FragmentCartBinding by AutoClearedValue(this)
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false).apply {
            viewModel = this@CartFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val cartAdapter = CartAdapter(this)

        binding.recyclerView.run {
            adapter = cartAdapter
            setHasFixedSize(true)
        }

        viewModel.cartListModels.observe(viewLifecycleOwner) {
            cartAdapter.submitList(it)
        }

        // Close cart button
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Toast
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onNavigateToSellerDetail(sellerId: String) {

    }

    override fun onNavigateToSellerMisc(sellerId: String) {

    }

    override fun onRemoveCartItem(userCartItem: UserCartItem) {
        viewModel.onRemoveCartItem(userCartItem)
    }
}