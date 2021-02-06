package com.foobarust.android.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
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
import com.foobarust.android.databinding.ActivityMainBinding
import com.foobarust.android.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            viewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }

        // Setup bottom navigation
        if (savedInstanceState == null) {
            val deepLink = intent.data
            intent.data = null
            setupBottomNavigation(deepLink)
        }

        // Setup toolbar item
        binding.toolbar.setOnMenuItemClickListener(this)

        // Show toast message
        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }

        // Show snack bar
        viewModel.snackBarMessage.observe(this) { message ->
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
        val deepLink = intent?.data
        if (deepLink != null) {
            intent.data = null
            navigateToDeepLink(deepLink)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun setupBottomNavigation(deepLink: Uri? = null) {
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

        // Navigate to deep link
        deepLink?.let {
            navigateToDeepLink(it)
        }

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
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                firebaseMessaging.deleteToken().await()
                firebaseMessaging.token.await()
            }

        }

        /*
        currentNavController?.value?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerSearchFragment()
        )

         */
    }

    private fun navigateToCartTimeOutDialog(property: CartTimeoutProperty) {
        CartTimeoutDialog.newInstance(property).show(
            supportFragmentManager,
            CartTimeoutDialog.TAG
        )
    }
}