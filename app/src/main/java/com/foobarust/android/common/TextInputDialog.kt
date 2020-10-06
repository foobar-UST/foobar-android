package com.foobarust.android.common

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.TextInputType.*
import com.foobarust.android.databinding.DialogTextInputBinding
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.setMaxLength
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.utils.PhoneUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TextInputDialog : BottomSheetDialogFragment() {

    private var binding: DialogTextInputBinding by AutoClearedValue(this)
    private val viewModel: TextInputViewModel by viewModels()
    private val args: TextInputDialogArgs by navArgs()
    @Inject lateinit var phoneUtil: PhoneUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTextInputBinding.inflate(inflater, container, false).apply {
            property = args.property
            lifecycleOwner = viewLifecycleOwner
        }

        // Set edit text default value
        binding.valueEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValue = text.toString()
        }

        // Return result when clicking save button
        binding.saveButton.setOnClickListener {
            if (viewModel.inputValue.isNullOrBlank()) {
                showShortToast(getString(R.string.profile_edit_field_null))
                return@setOnClickListener
            }

            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                args.property.editId, viewModel.inputValue
            )

            dismiss()
        }

        // Cancel button
        binding.cancelButton.setOnClickListener { dismiss() }

        setupEditTextStyle()

        return binding.root
    }

    private fun setupEditTextStyle() {
        // Input type
        binding.valueEditText.inputType = when (args.property.type) {
            NORMAL -> InputType.TYPE_CLASS_TEXT
            NAME -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            PHONE_NUM -> InputType.TYPE_CLASS_NUMBER
        }

        // Set phone number input field constraints
        if (args.property.type == PHONE_NUM) {
            binding.valueEditText.setMaxLength(8)
            binding.valueTextInputLayout.prefixText = "${phoneUtil.getPhoneNumPrefixString()} "
        }
    }
}