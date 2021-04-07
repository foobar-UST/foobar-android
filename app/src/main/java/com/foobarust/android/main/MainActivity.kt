package com.foobarust.android.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.foobarust.android.*
import com.foobarust.android.checkout.CartTimeoutDialog
import com.foobarust.android.databinding.ActivityMainBinding
import com.foobarust.android.seller.SellerFragmentDirections
import com.foobarust.android.tutorial.TutorialFragment
import com.foobarust.android.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private val navGraphIds = listOf(
    R.navigation.navigation_seller,
    R.navigation.navigation_order,
    R.navigation.navigation_explore,
    R.navigation.navigation_settings
)

private val topLevelDestinations = listOf(
    R.id.sellerFragment,
    R.id.orderFragment,
    R.id.exploreFragment,
    R.id.settingsFragment
)

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currentNavController: LiveData<NavController>? = null
    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Foobar_DayNight)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            root.applyLayoutFullscreen()
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        // Setup bottom navigation
        if (savedInstanceState == null) {
            viewModel.onDispatchDynamicLink(dynamicLink = intent.data)
            intent.data = null

            setupBottomNavigation()
        }

        // Setup toolbar item
        binding.toolbar.setOnMenuItemClickListener(this)

        // Navigate to cart
        binding.cartBottomBar.cartBottomBarCardView.setOnClickListener {
            currentNavController?.value?.navigate(
                NavigationSellerDirections.actionGlobalCheckoutFragment()
            )
        }

        // Register content launcher
        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val extension = it.getFileExtension(this)
                viewModel.onUploadUserPhoto(uri = it.toString(), extension = extension ?: "")
            }
        }

        // Navigate to cart timeout dialog
        lifecycleScope.launchWhenStarted {
            viewModel.navigateToCartTimeout.collect {
                showCartTimeoutDialog(it)
            }
        }

        // Navigate to onboarding tutorial
        lifecycleScope.launchWhenCreated {
            viewModel.navigateToTutorial.collect {
                showTutorialFragment()
            }
        }

        // Show toast message
        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        // Show snack bar
        lifecycleScope.launchWhenStarted {
            viewModel.snackBarMessage.collect {
                showSnackbarMessage(it)
            }
        }

        // Navigate to deep link destination
        lifecycleScope.launchWhenStarted {
            viewModel.deepLink.collect {
                navigateToDeepLink(it)
            }
        }

        // Register get image content launcher (Used for uploading user profile photo)
        lifecycleScope.launchWhenStarted {
            viewModel.getUserPhoto.collect {
                getContentLauncher.launch("image/*")
            }
        }

        // Setup cart bottom bar
        lifecycleScope.launchWhenStarted {
            viewModel.userCart.collect { userCart ->
                if (userCart == null) return@collect

                with(binding.cartBottomBar) {
                    cartItemsCountTextView.text = getString(
                        R.string.cart_bottom_bar_format_items_count,
                        userCart.itemsCount
                    )
                    cartTotalPriceTextView.text = getString(
                        R.string.cart_bottom_bar_format_total_price,
                        userCart.totalCost
                    )
                }
            }
        }

        // Show cart bottom bar
        lifecycleScope.launchWhenStarted {
            viewModel.showCartBottomBar.collect {
                binding.cartBottomBar.root.isVisible = it
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

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_seller_search -> navigateToSellerSearch()
        }

        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // Navigate to deep link
        viewModel.onDispatchDynamicLink(dynamicLink = intent?.data)
        intent?.data = null
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun setupBottomNavigation() {
        val navController = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
            navReselected = { viewModel.onScrollToTop() }
        )

        val listener = NavController.OnDestinationChangedListener { controller, destination, _ ->
            viewModel.onCurrentDestinationChanged(
                graphId = controller.graph.id,
                destinationId = destination.id
            )

            if (destination.id in topLevelDestinations) {
                setupTopLevelDestinations(destination.id)
            } else {
                setupInnerDestinations()
            }
        }

        navController.observe(this) { controller ->
            // Setup views for different tab navigation
            controller.registerOnDestinationChangedListener(listener)
        }

        currentNavController = navController
    }

    private fun navigateToDeepLink(deepLink: Uri) {
        binding.bottomNavigationView.navigateDeeplink(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            uri = deepLink
        )
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

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showCartTimeoutDialog(cartItemsCount: Int) {
        CartTimeoutDialog.newInstance(cartItemsCount)
            .show(supportFragmentManager, CartTimeoutDialog.TAG)
    }

    private fun showTutorialFragment() {
        TutorialFragment.newInstance()
            .show(supportFragmentManager, TutorialFragment.TAG)
    }
}