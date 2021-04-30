package com.foobarust.android.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentProfileBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.settings.ProfileListModel.ProfileEditItemModel
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : FullScreenDialogFragment(), ProfileAdapter.ProfileAdapterListener {

    private var binding: FragmentProfileBinding by AutoClearedValue(this)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var textInputResultObserver: LifecycleEventObserver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setLayoutFullscreen()

        binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
            profileRecyclerView.applySystemWindowInsetsPadding(applyBottom = true)
        }

        // Setup recycler view
        val profileAdapter = ProfileAdapter(this)

        binding.profileRecyclerView.run {
            adapter = profileAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileListModels.collect {
                profileAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.profileUiState.collect { uiState ->
                binding.loadingProgressBar.hideIf(uiState !is ProfileUiState.Loading)

                if (uiState is ProfileUiState.Error) {
                    showShortToast(uiState.message)
                }
            }
        }

        // Navigate to text input bottom sheet
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.navigateToTextInput.collect {
                // Observe for edit result once the text input dialog is opened
                subscribeTextInputResult(editItemId = it.id)
                findNavController(R.id.profileFragment)?.navigate(
                    ProfileFragmentDirections.actionProfileFragmentToTextInputDialog(it)
                )
            }
        }

        // Show snack bar message
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.snackBarMessage.collect {
                showMessageSnackBar(it)
            }
        }

        // Dismiss dialog
        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.profileFragment)?.navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove observer when the view is destroyed
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                unsubscribeTextInputResult(navBackStackEntry)
            }
        })
    }

    override fun onChangeProfilePhoto() {
        mainViewModel.onPickUserPhoto()
    }

    override fun onEditProfileItem(editItemModel: ProfileEditItemModel) {
        profileViewModel.onNavigateToTextInput(editItemModel)
    }

    private fun subscribeTextInputResult(editItemId: String) {
        // After a configuration change or process death, the currentBackStackEntry
        // points to the dialog destination, so you must use getBackStackEntry()
        // with the specific ID of your destination to ensure we always
        // get the right NavBackStackEntry
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)

        // Remove existing observer
        unsubscribeTextInputResult(navBackStackEntry)

        // Attach new result observer
        textInputResultObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (navBackStackEntry.savedStateHandle.contains(editItemId)) {
                    val result = navBackStackEntry.savedStateHandle.get<String>(editItemId)
                    result?.let {
                        updateInputResult(editItemId = editItemId, result = it)
                    }
                    navBackStackEntry.savedStateHandle.remove<String>(editItemId)
                }

                // Remove observer after obtaining the result
                unsubscribeTextInputResult(navBackStackEntry)
            }
        }.also {
            navBackStackEntry.lifecycle.addObserver(it)
        }
    }

    private fun unsubscribeTextInputResult(navBackStackEntry: NavBackStackEntry) {
        textInputResultObserver?.let {
            navBackStackEntry.lifecycle.removeObserver(it)
        }
    }

    private fun updateInputResult(editItemId: String, result: String) {
        when (editItemId) {
            EDIT_PROFILE_NAME -> profileViewModel.updateUserName(result)
            EDIT_PROFILE_PHONE_NUMBER -> profileViewModel.updateUserPhoneNum(result)
        }
    }

    private fun showMessageSnackBar(message: String) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}