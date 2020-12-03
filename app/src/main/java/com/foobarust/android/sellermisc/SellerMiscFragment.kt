package com.foobarust.android.sellermisc

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSellerMiscBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.drawDivider
import com.foobarust.android.utils.themeColor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created by kevin on 10/11/20
 */

@AndroidEntryPoint
class SellerMiscFragment : DialogFragment() {

    private var binding: FragmentSellerMiscBinding by AutoClearedValue(this)
    private val viewModel: SellerMiscViewModel by viewModels()
    private val args: SellerMiscFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Foobar_Dialog_Fullscreen_DayNight)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerMiscBinding.inflate(inflater, container, false).apply {
            viewModel = this@SellerMiscFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Receive property from nav args and pass to view model
        viewModel.onUpdateMiscProperty(args.sellerMiscProperty)

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        // Setup background
        val backgroundShapeDrawable = MaterialShapeDrawable(
            requireContext(),
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                requireContext().themeColor(R.attr.colorSurface)
            )
            elevation = resources.getDimension(R.dimen.elevation_xmedium)

            initializeElevationOverlay(requireContext())
        }

        binding.bottomSheetLayout.background = backgroundShapeDrawable

        // Setup map view
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launch {
            mapFragment.awaitMap().run {
                val targetLocation = LatLng(
                    viewModel.miscProperty.latitude,
                    viewModel.miscProperty.longitude
                )

                addMarker {
                    position(targetLocation)
                }

                moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f))
            }
        }

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

        return binding.root
    }
}