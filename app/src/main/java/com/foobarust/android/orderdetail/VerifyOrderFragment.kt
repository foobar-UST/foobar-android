package com.foobarust.android.orderdetail

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.R
import com.foobarust.android.databinding.FragmentVerifyOrderBinding
import com.foobarust.android.shared.FullScreenDialogFragment
import com.foobarust.android.utils.AutoClearedValue
import com.foobarust.android.utils.applySystemWindowInsetsPadding
import com.foobarust.android.utils.findNavController
import com.foobarust.android.utils.setLayoutFullscreen
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/6/21
 */

private const val QRCODE_SIZE = 512

@AndroidEntryPoint
class VerifyOrderFragment : FullScreenDialogFragment() {

    private var binding: FragmentVerifyOrderBinding by AutoClearedValue(this)
    private val viewModel: VerifyOrderViewModel by viewModels()
    private val navArgs: VerifyOrderFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onObserveOrderVerified(navArgs.orderId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setLayoutFullscreen()

        binding = FragmentVerifyOrderBinding.inflate(inflater, container, false).apply {
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController(R.id.verifyOrderFragment)?.navigateUp()
        }

        binding.qrCodeImageView.setImageBitmap(
            generateQRCodeBitmap(navArgs.verifyCode)
        )

        // Return when the order is verified
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderVerified.collect { isVerified ->
                if (isVerified) {
                    findNavController(R.id.verifyOrderFragment)?.navigateUp()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    private fun generateQRCodeBitmap(content: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE)

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                val pixel = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, pixel)
            }
        }

        return bitmap
    }
}