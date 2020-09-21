package com.foobarust.android.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentProfileBinding
import com.foobarust.android.profile.ProfileListModel.ProfileEditModel
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.showShortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(), ProfileAdapter.ProfileAdapterListener {

    private var binding: FragmentProfileBinding by AutoClearedValue(this)
    private var currentEditResultObserver: LifecycleEventObserver? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            viewModel = this@ProfileFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Setup recycler view
        val profileAdapter = ProfileAdapter(this)

        binding.profileRecyclerView.run {
            adapter = profileAdapter
            setHasFixedSize(true)
        }
        viewModel.profileItems.observe(viewLifecycleOwner) {
            profileAdapter.submitList(it)
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            showShortToast(it)
        }

        // Navigate to text input bottom sheet
        viewModel.navigateToTextInput.observe(viewLifecycleOwner) {
            findNavController(R.id.profileFragment)?.navigate(
                ProfileFragmentDirections.actionProfileFragmentToTextInputBottomSheet(it)
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove observer when the view is destroyed
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeEditResultObserver(navBackStackEntry)
            }
        })
    }

    override fun onProfileAvatarClicked() {
        requireActivity().registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.updateUserPhoto(it.toString()) }
        }.launch("image/*")
    }

    override fun onProfileEditItemClicked(editModel: ProfileEditModel) {
        subscribeForEditResult(editId = editModel.id)
        viewModel.onNavigateToTextInput(editModel)
    }

    private fun subscribeForEditResult(editId: String) {
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.profileFragment)
        removeEditResultObserver(navBackStackEntry)

        // Attach new observer
        currentEditResultObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (navBackStackEntry.savedStateHandle.contains(editId)) {
                    val result = navBackStackEntry.savedStateHandle.get<String>(editId)
                    result?.let { updateEditResult(editId, it) }

                    navBackStackEntry.savedStateHandle.remove<String>(editId)
                }

                removeEditResultObserver(navBackStackEntry)
            }
        }.also {
            navBackStackEntry.lifecycle.addObserver(it)
        }
    }

    private fun removeEditResultObserver(navBackStackEntry: NavBackStackEntry) {
        currentEditResultObserver?.let {
            navBackStackEntry.lifecycle.removeObserver(it)
        }
    }

    private fun updateEditResult(editId: String, result: String) {
        when (editId) {
            EDIT_PROFILE_NAME -> viewModel.updateUserName(result)
            EDIT_PROFILE_PHONE_NUMBER -> viewModel.updateUserPhoneNum(result)
        }
    }
}