/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.qrReader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.databinding.FragmentQrReaderBinding
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.base.BaseFragment
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject


/**
 * [androidx.fragment.app.Fragment] implementation of QR Reader view where the user can scan a QR code.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class QrReaderFragment : BaseFragment(), BarcodesReceivedListener {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentQrReaderBinding? = null
    private val binding get() = _binding!!

    /**
     * The view model instance for this view.
     */
    override val viewModel: QrReaderViewModel by viewModels()

    /**
     * A [ProcessCameraProvider] instance for camera initialization.
     */
    private var cameraProvider: ProcessCameraProvider? = null

    /**
     * A [ExecutorService] instance for camera initialization.
     */
    private lateinit var cameraExecutor: ExecutorService

    /**
     * An [ActivityResultLauncher] instance for permission requests.
     */
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    /**
     * An instance of a [ErrorHandler] implementation. Received errors will be passed to this error
     * handler instance.
     */
    @Inject
    lateinit var errorHandler: ErrorHandler
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Request camera permissions
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGrantedMap ->
                if (!isGrantedMap.containsValue(false)) {
                    startCamera()
                }
            }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            if (shouldShowRequestPermissionsRationale()) {
                showRationale()
            } else {
                requestPermissions()
            }
        }
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region Private Interface
    /**
     * Starts camera.
     *
     * The camera must be started only when the user permitted all necessary permissions.
     */
    private fun startCamera() {
        val baseContext = activity?.baseContext ?: return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(baseContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer(this))
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exception: Exception) {
                errorHandler.handle(exception)
            }

        }, ContextCompat.getMainExecutor(baseContext))
    }

    /**
     * Check if all necessary permissions are granted by the user.
     *
     * @return The result pf permission check.
     */
    private fun allPermissionsGranted(): Boolean {
        val baseContext = activity?.baseContext ?: return false
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    /**
     * Request permissions for camera usage.
     */
    private fun requestPermissions() {
        requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
    }

    /**
     * Check if rational dialog should show or not.
     *
     * @return The result pf permission check.
     */
    private fun shouldShowRequestPermissionsRationale(): Boolean {
        return REQUIRED_PERMISSIONS.any { shouldShowRequestPermissionRationale(it) }
    }

    /**
     * Shows rational dialog to the user.
     */
    private fun showRationale() {
        val context = context ?: return
        AlertDialog.Builder(context)
            .setMessage(R.string.rational_dialog_message_camera)
            .setPositiveButton(R.string.rational_dialog_ok_button_title) { _, _ ->
                run {
                    requestPermissions()
                }
            }
            .setNegativeButton(R.string.rational_dialog_cancel_button_title, null)
            .show()
    }
    //endregion

    //region BarcodesReceivedListener
    override fun onBarcodesReceived(barcodes: List<Barcode>) {
        if (barcodes.isNotEmpty()) {
            barcodes.first().rawValue?.let {
                Timber.asTree().sdk(String.format("Barcode read: %s", it))
                cameraProvider?.unbindAll()
                viewModel.decodeOutOfBandPayload(it)
            }
        }
    }
    //endregion

    //region Companion Object
    /**
     * Constants.
     */
    companion object {
        /**
         * List of necessary permissions for camera usage.
         */
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).toTypedArray()
    }
    //endregion
}
