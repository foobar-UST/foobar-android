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
            setupBottomNavigation()
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
            if (savedInstanceState == null) {
                navigateToCartTimeOutDialog(property = it)
            }
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
        setupBottomNavigation()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // When the user is signed in through deep link, restart MainActivity to reload the data.
        finish()
        startActivity(intent)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_seller_search -> navigateToSellerSearch()
        }

        return true
    }

    private fun setupBottomNavigation() {
        val navController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = viewModel.navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
            navReselected = { viewModel.onScrollToTop() }
        )

        val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            viewModel.onCurrentDestinationChanged(
                graphId = controller.graph.id,
                destinationId = destination.id
            )

            if (destination.id in viewModel.topLevelDestinations) {
                setupTopLevelDestinations(destination.id)
            } else {
                setupInnerDestinations()

            }
        }

        navController.observe(this) { controller ->
            // Setup toolbar
            binding.toolbar.setupWithNavController(
                navController = controller,
                configuration = AppBarConfiguration(
                    setOf(controller.graph.startDestination)
                )
            )

            // Restore app bar height
            binding.appBarLayout.setExpanded(true, true)

            // Setup views for different tab navigation
            controller.registerOnDestinationChangedListener(listener)
        }

        currentNavController = navController

        // TODO: Setup badge
        with(binding.bottomNavigationView) {
            getOrCreateBadge(R.id.navigation_explore).isVisible = true
        }
    }

    private fun setupTopLevelDestinations(destinationId: Int) {
        if (destinationId == R.id.sellerFragment) {
            binding.toolbar.inflateMenu(R.menu.menu_toolbar_main)
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun setupInnerDestinations() {
        binding.toolbar.menu.clear()
    }

    private fun navigateToSellerSearch() {
        currentNavController?.value?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerSearchFragment()
        )
    }

    private fun navigateToCartTimeOutDialog(property: CartTimeoutProperty) {
        CartTimeoutDialog.newInstance(property).show(
            supportFragmentManager,
            CartTimeoutDialog.TAG
        )
    }
}