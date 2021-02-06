package com.foobarust.android.common

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.common.TextInputType.*
import com.foobarust.android.databinding.DialogTextInputEntryBinding
import com.foobarust.android.utils.showShortToast
import com.foobarust.domain.usecases.common.GetFormattedPhoneNumUseCase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputDialog : DialogFragment() {

    private var binding: DialogTextInputEntryBinding? = null
    private val viewModel: TextInputViewModel by viewModels()
    private val args: TextInputDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogTextInputEntryBinding.inflate(
            LayoutInflater.from(requireContext())
        ).apply {
            textInputProperty = args.property

            viewModel.onUpdateTextInputProperty(args.property)

            // Set dialog input type
            valueEditText.inputType = when (args.property.type) {
                NORMAL -> InputType.TYPE_CLASS_TEXT
                NAME -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                PHONE_NUM -> InputType.TYPE_CLASS_NUMBER
            }

            // Set input constraints
            if (args.property.type == PHONE_NUM) {
                // Set max length
                valueEditText.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(GetFormattedPhoneNumUseCase.LENGTH)
                )
                valueTextInputLayout.prefixText = GetFormattedPhoneNumUseCase.AREA_CODE
            }
        }

        // Update input value
        binding?.valueEditText?.doOnTextChanged { text, _, _, _ ->
            viewModel.inputValue = text.toString()
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(args.property.title)
            .setView(binding?.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                // Check if input valid
                if (!viewModel.isInputValid()) {
                    showShortToast(getString(R.string.profile_edit_field_input_invalid))
                    return@setPositiveButton
                }

                // Send back input value
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    args.property.id,
                    viewModel.inputValue
                )

                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}