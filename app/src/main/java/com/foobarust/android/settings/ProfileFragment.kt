package com.foobarust.android.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.common.FullScreenDialogFragment
import com.foobarust.android.databinding.FragmentProfileBinding
import com.foobarust.android.main.MainViewModel
import com.foobarust.android.settings.ProfileListModel.ProfileEditModel
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.getFileExtension
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

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
        binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            viewModel = this@ProfileFragment.profileViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val profileAdapter = ProfileAdapter(this)

        binding.profileRecyclerView.run {
            adapter = profileAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
        }

        profileViewModel.profileListModels.observe(viewLifecycleOwner) {
            profileAdapter.submitList(it)
        }

        // Navigate to text input bottom sheet
        profileViewModel.navigateToTextInput.observe(viewLifecycleOwner) {
            // Observe for edit result once the text input dialog is opened
            subscribeTextInputResult(editItemId = it.id)

            findNavController(R.id.profileFragment)?.navigate(
                ProfileFragmentDirections.actionProfileFragmentToTextInputDialog(it)
            )
        }

        // Show toast message
        profileViewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Dismiss dialog
        binding.toolbar.setNavigationOnClickListener { dismiss() }

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

    override fun onProfileAvatarClicked() {
        // Pick image
        requireActivity().registerForActivityResult(ActivityResultContracts.GetContent()) { returnUri: Uri? ->
            returnUri?.let {
                val fileExtension = it.getFileExtension(requireContext())
                mainViewModel.onUploadUserPhoto(uri = it.toString(), extension = fileExtension ?: "")
            }
        }.launch("image/*")
    }

    override fun onProfileEditItemClicked(editModel: ProfileEditModel) {
        profileViewModel.onNavigateToTextInput(editModel)
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
                        updateInputResult(
                            editItemId = editItemId,
                            result = it
                        )
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
}