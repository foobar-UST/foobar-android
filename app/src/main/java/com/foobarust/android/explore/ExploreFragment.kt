package com.foobarust.android.explore

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.databinding.FragmentExploreBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.shared.PagingLoadStateAdapter
import com.foobarust.android.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploreFragment : Fragment(), NotificationsAdapter.NotificationsAdapterListener {

    private var binding: FragmentExploreBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        // Setup recycler view
        val notificationsAdapter = NotificationsAdapter(this)

        binding.exploreRecyclerView.run {
            adapter = notificationsAdapter.withLoadStateFooter(
                footer = PagingLoadStateAdapter { notificationsAdapter.retry() }
            )
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            exploreViewModel.notificationListModels.collectLatest {
                notificationsAdapter.submitData(it)
            }
        }

        // Retry
        binding.loadErrorLayout.retryButton.setOnClickListener {
            notificationsAdapter.refresh()
        }

        // Control views with respect to load states
        notificationsAdapter.addLoadStateListener { loadStates ->
            with(loadStates) {
                updateViews(
                    mainLayout = binding.exploreRecyclerView,
                    errorLayout = binding.loadErrorLayout.loadErrorLayout,
                    progressBar = binding.loadingProgressBar,
                    swipeRefreshLayout = binding.swipeRefreshLayout
                )
                anyError()?.let {
                    showShortToast(it.toString())
                }
            }
        }

        // Start swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            notificationsAdapter.refresh()
        }

        // Scroll to top when the tab is reselected
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.scrollToTop.collect {
                binding.exploreRecyclerView.smoothScrollToTop()
            }
        }

        // Show toast
        viewLifecycleOwner.lifecycleScope.launch {
            exploreViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        return binding.root
    }

    override fun onNotificationClicked(link: String) {
        mainViewModel.onDispatchDynamicLink(Uri.parse(link))
    }
}