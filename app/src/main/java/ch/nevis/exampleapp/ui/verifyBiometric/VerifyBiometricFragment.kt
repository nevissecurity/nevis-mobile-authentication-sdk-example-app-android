/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyBiometric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentVerifyBiometricBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.verifyBiometric.model.VerifyBiometricViewData
import ch.nevis.exampleapp.ui.verifyBiometric.parameter.VerifyBiometricNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricPromptOptions
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodePromptOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment implementation of Verify User view where the user can verify her-/himself with
 * fingerprint, face ID or device passcode.
 */
@AndroidEntryPoint
class VerifyBiometricFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentVerifyBiometricBinding? = null
    private val binding get() = _binding!!

    /**
     * The view model instance for this view.
     */
    override val viewModel: VerifyBiometricViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: VerifyBiometricFragmentArgs by navArgs()

    /**
     * [BiometricPromptOptions] object that is required in case of biometric user verification for
     * the dialog shown by the OS.
     */
    private lateinit var biometricPromptOptions: BiometricPromptOptions

    /**
     * [DevicePasscodePromptOptions] object that is required in case of device passcode user verification for
     * the dialog shown by the OS.
     */
    private lateinit var devicePasscodePromptOptions: DevicePasscodePromptOptions
    //endregion

    //region Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBiometricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biometricPromptOptions = BiometricPromptOptions.builder()
            .title(getString(R.string.verify_user_biometric_prompt_title))
            .cancelButtonText(getString(R.string.verify_user_biometric_prompt_cancel_button_title))
            .build()

        devicePasscodePromptOptions = DevicePasscodePromptOptions.builder()
            .title(getString(R.string.verify_user_device_pass_code_prompt_title))
            .build()

        viewModel.updateViewModel(
            navigationArguments.parameter,
            biometricPromptOptions,
            devicePasscodePromptOptions
        )

        // Override back button handling.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CancelOperationOnBackPressedCallback(viewModel)
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeOsListening()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseOsListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is VerifyBiometricNavigationParameter) {
            viewModel.updateViewModel(
                parameter,
                biometricPromptOptions,
                devicePasscodePromptOptions
            )
            return true
        }
        return false
    }

    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)
        if (viewData is VerifyBiometricViewData) {
            binding.errorMessageTextView.text = viewData.errorMessage
        }
    }
    //endregion
}