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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currentNavController: LiveData<NavController>? = null

    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Foobar_DayNight)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
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

        // Launch get content
        lifecycleScope.launchWhenStarted {
            viewModel.getUserPhoto.collect {
                getContentLauncher.launch("image/*")
            }
        }

        // Register get content launcher
        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val fileExtension = it.getFileExtension(this)
                viewModel.onUploadUserPhoto(
                    uri = it.toString(),
                    extension = fileExtension ?: ""
                )
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

        val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
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
            // Setup toolbar title
            binding.toolbar.setupWithNavController(
                navController = controller,
                configuration = AppBarConfiguration(
                    setOf(controller.graph.startDestination)
                )
            )

            // Setup views for different tab navigation
            controller.registerOnDestinationChangedListener(listener)
        }

        currentNavController = navController

        // TODO: Setup badge
        with(binding.bottomNavigationView) {
            getOrCreateBadge(R.id.navigation_explore).isVisible = true
        }
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