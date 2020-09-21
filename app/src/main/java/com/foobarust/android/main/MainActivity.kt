package com.foobarust.android.main

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
import com.foobarust.android.R
import com.foobarust.android.databinding.ActivityMainBinding
import com.foobarust.android.seller.SellerFragmentDirections
import com.foobarust.android.utils.setupWithNavController
import com.foobarust.android.utils.showShortToast
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

        binding.toolbar.setOnMenuItemClickListener(this)

        viewModel.toastMessage.observe(this) {
            showShortToast(it)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(
            R.navigation.navigation_seller,
            R.navigation.navigation_order,
            R.navigation.navigation_explore,
            R.navigation.navigation_settings
        )

        val controller = binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.fragment_container,
            intent = intent
        )

        controller.observe(this) {
            // Setup toolbar
            binding.toolbar.setupWithNavController(
                navController = it,
                configuration = AppBarConfiguration(setOf(it.graph.startDestination))
            )

            // Setup views for different tab navigation
            when (it.graph.id) {
                R.id.navigation_seller -> setupViewsForSeller()
                else -> setupViewsForOthers()
            }
        }
        currentNavController = controller

        // TODO: show badge
        binding.bottomNavigationView.getOrCreateBadge(R.id.navigation_explore).run {
            isVisible = true
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.action_seller_search -> showSellerSearch()
        }

        return true
    }

    private fun setupViewsForSeller() {
        binding.toolbar.inflateMenu(R.menu.menu_toolbar_main)
    }

    private fun setupViewsForOthers() {
        binding.toolbar.menu.clear()
    }

    private fun showSellerSearch() {
        currentNavController?.value?.navigate(
            SellerFragmentDirections.actionSellerFragmentToSellerSearchFragment()
        )
    }
}