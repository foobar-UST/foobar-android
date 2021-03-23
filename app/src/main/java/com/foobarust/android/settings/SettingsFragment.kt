package com.foobarust.android.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSettingsBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), SettingsAdapter.SettingsAdapterListener {

    private var binding: FragmentSettingsBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var packageManager: PackageManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Setup recycler view
        val settingsAdapter = SettingsAdapter(this)

        binding.settingsRecyclerView.run {
            adapter = settingsAdapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.settingsListModels.collect {
                settingsAdapter.submitList(it)
            }
        }

        // Ui state
        settingsViewModel.settingsUiState.observe(viewLifecycleOwner) {
            binding.loadingProgressBar.isVisible = it == SettingsUiState.LOADING
        }

        // Navigate to SignInActivity
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.navigateToSignIn.collect {
                findNavController(R.id.settingsFragment)?.navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToAuthActivity()
                )
            }
        }

        // Navigate to ProfileFragment
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.navigateToProfile.collect {
                findNavController(R.id.settingsFragment)?.navigate(
                    SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
                )
            }
        }

        // Show signed out message snack bar
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.isUserSignedOut.collect {
                mainViewModel.onShowSnackBarMessage(
                    message = getString(R.string.auth_signed_out_message)
                )
            }
        }

        // Show toast message
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.toastMessage.collect {
                showShortToast(it)
            }
        }

        return binding.root
    }

    override fun onProfileClicked(isSignedIn: Boolean) {
        settingsViewModel.onNavigateToProfileOrSignIn(isSignedIn)
    }

    override fun onSectionItemClicked(sectionId: String) {
        when (sectionId) {
            SETTINGS_NOTIFICATIONS -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToNotificationFragment()
            )
            SETTINGS_FAVORITE -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToFavoriteFragment()
            )
            SETTINGS_CONTACT_US -> sendContactUsEmail()
            SETTINGS_TERMS_CONDITIONS -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToLicenseFragment()
            )
            SETTINGS_FEATURES -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToTutorialFragment()
            )
            SETTINGS_SIGN_OUT -> showSignOutConfirmDialog()
        }
    }

    private fun sendContactUsEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kthon@connect.ust.hk"))
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showShortToast(getString(R.string.error_resolve_activity_failed))
        }
    }

    private fun showSignOutConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sign_out_dialog_title))
            .setMessage(getString(R.string.sign_out_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> settingsViewModel.onUserSignOut() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}