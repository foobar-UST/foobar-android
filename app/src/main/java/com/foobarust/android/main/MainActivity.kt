package com.foobarust.android.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.foobarust.android.NavigationExploreDirections
import com.foobarust.android.NavigationOrderDirections
import com.foobarust.android.NavigationSellerDirections
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityMainBinding
import com.foobarust.android.seller.SellerFragmentDirections
import com.foobarust.android.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            setupBottomNavigation()
        }

        // Setup toobar item
        binding.toolbar.setOnMenuItemClickListener(this)

        // Show toast message
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }

        // Open cart
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener { showExpandedCart() }

        // Show or hide cart bottom bar
        viewModel.showCartBottomBar.observe(this) {
            if (it) showCartBottomBar() else hideCartBottomBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigation()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // When the user is signed in, restart MainActivity to reload the data.
        finish()
        startActivity(intent)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_seller_search -> showSellerSearch()
        }

        return true
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(
            R.navigation.navigation_seller,
            R.navigation.navigation_order,
            R.navigation.navigation_explore,
            R.navigation.navigation_settings
        )

        val navController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.fragment_container,
            intent = intent,
            itemReselected = { viewModel.onTabScrollToTop() }
        )

        val topLevelDestinations = listOf(
            R.id.sellerFragment,
            R.id.orderFragment,
            R.id.exploreFragment,
            R.id.settingsFragment
        )

        val listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                setupViewsForNavGraph(controller.graph.id)

                if (controller.currentDestination?.id in topLevelDestinations) {
                    setupViewsForTopLevelDestinations(controller.currentDestination?.id)
                } else {
                    setupViewsForInnerDestinations()
                }
            }

        navController.observe(this) {
            // Setup toolbar
            binding.toolbar.setupWithNavController(
                navController = it,
                configuration = AppBarConfiguration(setOf(it.graph.startDestination))
            )

            // Setup views for different tab navigation
            it.registerOnDestinationChangedListener(listener)

            // Restore app bar height
            binding.appBarLayout.setExpanded(true, true)
        }

        currentNavController = navController

        // TODO: show badge
        binding.bottomNavigationView.getOrCreateBadge(R.id.navigation_explore).run {
            isVisible = true
        }
    }

    private fun setupViewsForNavGraph(currentGraphId: Int?) {
        // Hide cart bottom bar in settings graph
        if (currentGraphId == R.id.navigation_settings) {
            viewModel.hideCartBottomBar()
        } else {
            viewModel.showCartBottomBar()
        }
    }

    private fun setupViewsForTopLevelDestinations(currentDestinationId: Int?) {
        // Show toolbar menu only in SellerFragment
        if (currentDestinationId == R.id.sellerFragment) {
            binding.toolbar.inflateMenu(R.menu.menu_toolbar_main)
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun setupViewsForInnerDestinations() {
        binding.toolbar.menu.clear()
        //viewModel.hideCartBottomBar()
    }

    private fun showSellerSearch() {
        currentNavController?.value?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerSearchFragment()
        )
    }

    private fun showCartBottomBar() {
        //binding.cartBottomBar.cartBottomBarCardView.slideUp()
        binding.cartBottomBar.cartBottomBarCardView.show()
    }

    private fun hideCartBottomBar() {
        //binding.cartBottomBar.cartBottomBarCardView.slideDown()
        binding.cartBottomBar.cartBottomBarCardView.hide()
    }

    private fun showExpandedCart() {
        val direction = when (currentNavController?.value?.graph?.id) {
            R.id.navigation_seller -> NavigationSellerDirections.actionGlobalCartFragment()
            R.id.navigation_order -> NavigationOrderDirections.actionGlobalCartFragment()
            R.id.navigation_explore -> NavigationExploreDirections.actionGlobalCartFragment()
            else -> throw IllegalStateException("Invalid direction for opening cart.")
        }

        currentNavController?.value?.navigate(direction)
    }
}