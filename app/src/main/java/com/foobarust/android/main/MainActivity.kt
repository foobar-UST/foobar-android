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
import com.foobarust.android.*
import com.foobarust.android.databinding.ActivityMainBinding
import com.foobarust.android.seller.SellerFragmentDirections
import com.foobarust.android.tutorial.TutorialFragment
import com.foobarust.android.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        ).apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }

        if (savedInstanceState == null) {
            setupNavigation()
        }

        // Setup toolbar item
        binding.toolbar.setOnMenuItemClickListener(this)

        // Show toast message
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }

        // Show snack bar
        viewModel.showSnackBarMessage.observe(this) { message ->
            Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
        }

        // Navigate to cart
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            currentNavController?.value?.navigate(
                NavigationSellerDirections.actionGlobalCheckoutFragment()
            )
        }

        // Navigate to cart timeout dialog
        viewModel.navigateToTimeoutDialog.observe(this) {
            showCartTimeoutDialog(property = it)
        }

        // Show onboarding tutorial
        viewModel.showOnboardingTutorial.observe(this) {
            showOnboardingTutorial()
        }

        // Launch chrome tab
        viewModel.launchCustomTab.observe(this) {
            if (!launchCustomTab(url = it)) {
                showShortToast(getString(R.string.error_resolve_activity_failed))
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupNavigation()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // When the user is signed in, restart MainActivity to reload the data.
        finish()
        startActivity(intent)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_seller_search -> navigateToSellerSearch()
        }

        return true
    }

    private fun setupNavigation() {
        val navGraphIds = listOf(
            R.navigation.navigation_seller,
            R.navigation.navigation_order,
            R.navigation.navigation_explore,
            R.navigation.navigation_settings
        )
        val navController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
            itemReselected = { viewModel.onScrollToTop() }
        )
        val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            // Keep track of the current graph and destination
            viewModel.onCurrentNavGraphChanged(controller.graph.id)
            viewModel.onCurrentDestinationChanged(destination.id)

            // Setup views for top level destinations
            if (destination.id in viewModel.topLevelDestinations) {
                setupTopLevelDestinationsViews(destination.id)
            } else {
                setupInnerDestinationsViews()
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

        // TODO: Setup badge
        binding.bottomNavigationView.getOrCreateBadge(R.id.navigation_explore).run {
            isVisible = true
        }
    }

    private fun setupTopLevelDestinationsViews(destinationId: Int) {
        when (destinationId) {
            R.id.sellerFragment -> binding.toolbar.inflateMenu(R.menu.menu_toolbar_main)
            else -> binding.toolbar.menu.clear()
        }
    }

    private fun setupInnerDestinationsViews() {
        binding.toolbar.menu.clear()
    }

    private fun navigateToSellerSearch() {
        currentNavController?.value?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerSearchFragment()
        )
    }

    private fun showCartTimeoutDialog(property: CartTimeoutProperty) {
        CartTimeoutDialog.newInstance(property).show(
            supportFragmentManager,
            CartTimeoutDialog.TAG
        )
    }

    private fun showOnboardingTutorial() {
        TutorialFragment().show(supportFragmentManager, TutorialFragment.TAG)
    }
}