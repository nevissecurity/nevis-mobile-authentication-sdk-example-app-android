/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentVerifyUserBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.verifyUser.model.VerifyUserViewData
import ch.nevis.exampleapp.ui.verifyUser.parameter.VerifyUserNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricPromptOptions
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodePromptOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment implementation of Verify User view where the user can verify her-/himself with
 * fingerprint, face ID or device passcode.
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class VerifyUserFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentVerifyUserBinding? = null
    private val binding get() = _binding!!

    /**
     * The view model instance for this view.
     */
    override val viewModel: VerifyUserViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: VerifyUserFragmentArgs by navArgs()

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
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biometricPromptOptions = BiometricPromptOptions.builder()
            .title(getString(R.string.verify_user_biometric_prompt_title))
            .cancelButtonText(getString(R.string.verify_user_biometric_prompt_cancel_button_title))
            .build()

        devicePasscodePromptOptions = DevicePasscodePromptOptions.builder()
            .title(getString(R.string.verify_user_device_passcode_prompt_title))
            .build()

        viewModel.updateViewModel(
            navigationArguments.parameter,
            biometricPromptOptions,
            devicePasscodePromptOptions
        )

        val title = getString(
            R.string.verify_user_title,
            getString(navigationArguments.parameter.authenticatorTitleResId)
        )
        binding.titleTextView.text = title

        binding.confirmButton.setOnClickListener {
            viewModel.verifyUser()
        }

        // Override back button handling.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CancelOperationOnBackPressedCallback(viewModel)
        )
    }

    /** @suppress */
    override fun onResume() {
        super.onResume()
        viewModel.resumeOsListening()
    }

    /** @suppress */
    override fun onPause() {
        super.onPause()
        viewModel.pauseOsListening()
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is VerifyUserNavigationParameter) {
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
        if (viewData is VerifyUserViewData) {
            val visibility = if (viewData.isFingerPrintVerification) View.VISIBLE else View.GONE
            binding.descriptionTextView.visibility = visibility
            binding.errorMessageTextView.text = viewData.errorMessage
        }
    }
    //endregion
}
