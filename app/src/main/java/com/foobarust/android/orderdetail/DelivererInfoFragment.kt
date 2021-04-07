package com.foobarust.android.orderdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentDelivererInfoBinding
import com.foobarust.android.shared.AppConfig.PHONE_NUM_PREFIX
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.loadGlideUrl
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.models.user.UserDelivery
import com.foobarust.domain.states.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/7/21
 */

@AndroidEntryPoint
class DelivererInfoFragment : BottomSheetDialogFragment() {

    private var binding: FragmentDelivererInfoBinding by AutoClearedValue(this)
    private val viewModel: DelivererInfoViewModel by viewModels()
    private val navArgs: DelivererInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchDelivererProfile(navArgs.delivererId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDelivererInfoBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.delivererProfile.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        setupViews(it.data)
                    }
                    is Resource.Error -> {
                        showShortToast(it.message)
                        findNavController(R.id.delivererInfoFragment)?.navigateUp()
                    }
                    is Resource.Loading -> Unit
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews(delivererInfo: UserDelivery) = binding.run {
        delivererPhotoImageView.loadGlideUrl(
            imageUrl = delivererInfo.photoUrl,
            circularCrop = true,
            placeholder = R.drawable.ic_user
        )
        delivererNameTextView.text = delivererInfo.name
        phoneNumTextView.text = PHONE_NUM_PREFIX + ' ' + delivererInfo.phoneNum
    }
}