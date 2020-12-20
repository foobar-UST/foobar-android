package com.foobarust.android.sellermisc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentSellerMiscBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.drawDivider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerMiscFragment : FullScreenDialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()
    private val args: SellerMiscFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onFetchSellerDetail(sellerId = args.sellerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerMiscFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Setup property list
        val sellerMiscAdapter = SellerMiscAdapter()

        binding.recyclerView.run {
            adapter = sellerMiscAdapter
            drawDivider(forViewType = R.layout.seller_misc_address_item)
            setHasFixedSize(true)
        }

        viewModel.sellerMiscListModels.observe(viewLifecycleOwner) {
            sellerMiscAdapter.submitList(it)
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            viewModel.onFetchSellerDetail(sellerId = args.sellerId)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup map view
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        viewModel.latLng.observe(viewLifecycleOwner) { latLng ->
            viewLifecycleOwner.lifecycleScope.launch {
                mapFragment.awaitMap().run {
                    addMarker { position(latLng) }
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }
}