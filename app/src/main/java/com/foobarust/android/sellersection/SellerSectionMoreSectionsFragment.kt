package com.foobarust.android.sellersection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.foobarust.android.R
import com.foobarust.android.common.PagingLoadStateAdapter
import com.foobarust.android.databinding.FragmentSellerSectionMoreSectionsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.anyError
import com.foobarust.android.utils.showShortToast
import com.foobarust.android.utils.updateViews
import com.foobarust.domain.models.seller.SellerSectionBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 1/3/21
 */

@AndroidEntryPoint
class SellerSectionMoreSectionsFragment : Fragment(),
    SellerSectionsAdapter.SellerOffCampusAdapterListener {

    private var binding: FragmentSellerSectionMoreSectionsBinding by AutoClearedValue(this)
    private val sectionViewModel: SellerSectionViewModel by hiltNavGraphViewModels(R.id.navigation_seller_section)
    private val moreSectionsViewModel: SellerSectionMoreSectionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            sectionViewModel.sectionDetail.collect { sectionDetail ->
                sectionDetail?.let {
                    moreSectionsViewModel.onFetchSellerSections(sectionDetail = it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerSectionMoreSectionsBinding.inflate(inflater, container, false)

        val concatAdapter = ConcatAdapter()
        val sectionsAdapter = SellerSectionsAdapter(this)

        concatAdapter.addAdapter(sectionsAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { sectionsAdapter.retry() }
        ))

        binding.recyclerView.run {
            adapter = sectionsAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            moreSectionsViewModel.sectionsListModels.collectLatest {
                sectionsAdapter.submitData(it)
            }
        }

        // Control views corresponding to load states
        sectionsAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.recyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.progressBar
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Retry button
        binding.loadErrorLayout.retryButton.setOnClickListener {
            sectionsAdapter.refresh()
        }

        // Observe dialog back press and navigate up
        sectionViewModel.backPressed.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onSellerSectionItemClicked(sectionBasic: SellerSectionBasic) {
        sectionViewModel.onNavigateToSellerSection(sectionId = sectionBasic.id)
    }

    override fun onSellerSectionItemLongClicked(
        view: View,
        sectionBasic: SellerSectionBasic
    ): Boolean {
        return true
    }
}