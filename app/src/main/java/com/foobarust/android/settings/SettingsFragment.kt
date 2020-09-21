package com.foobarust.android.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentSettingsBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), SettingsAdapter.SettingsAdapterListener {

    private var binding: FragmentSettingsBinding by AutoClearedValue(this)
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Setup recyclerView
        val settingsAdapter = SettingsAdapter(this)

        binding.itemRecyclerView.run {
            adapter = settingsAdapter
            //drawDivider(forViewType = R.layout.item_settings_profile)
            setHasFixedSize(true)
        }

        viewModel.settingsItems.observe(viewLifecycleOwner) {
            settingsAdapter.submitList(it)
        }

        // Navigate to SignInActivity
        viewModel.navigateToSignIn.observe(viewLifecycleOwner) {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToSignInActivity()
            )
        }

        // Navigate to ProfileFragment
        viewModel.navigateToProfile.observe(viewLifecycleOwner) {
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
            )
        }

        // Observe toast message
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        return binding.root
    }

    override fun onSettingsUserProfileClicked() {
        viewModel.onUserAccountCardClicked()
    }

    override fun onSettingsSectionClicked(sectionId: String) {
        when (sectionId) {
            SETTINGS_NOTIFICATIONS -> findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToNotificationFragment()
            )
            SETTINGS_FAVORITE -> findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToFavoriteFragment()
            )
            SETTINGS_CONTACT_US -> sendContactUsEmail()
            SETTINGS_TERMS_CONDITIONS -> findNavController(R.id.settingsFragment)?.navigate(
                SettingsFragmentDirections.actionSettingsFragmentToLicenseFragment()
            )
            SETTINGS_FEATURES -> findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToOnboardingActivity()
            )
            SETTINGS_SIGN_OUT -> showSignOutConfirmDialog()
            SETTINGS_ORDER_HISTORY -> findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToOrderHistoryFragment()
            )
        }
    }

    private fun sendContactUsEmail() {
        // TODO: build a custom email with subject and content prefixes
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kthon@connect.ust.hk"))
            //putExtra(Intent.EXTRA_SUBJECT, "Email Subject")
            //putExtra(Intent.EXTRA_TEXT, "Email Content")
        }

        // Start email intent and check for no app found exception
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showShortToast(getString(R.string.activity_not_found_message))
        }
    }

    private fun showSignOutConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sign_out_dialog_title))
            .setMessage(getString(R.string.sign_out_dialog_message))
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.signOut() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}