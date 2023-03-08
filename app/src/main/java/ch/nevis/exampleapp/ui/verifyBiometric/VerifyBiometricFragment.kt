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
import ch.nevis.mobile.sdk.api.util.Optional
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment implementation of Verify Biometric view where the user can verify her-/himself with
 * fingerprint or face ID.
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
    private val biometricPromptOptions = object : BiometricPromptOptions {
        override fun title(): String {
            return getString(R.string.verify_biometric_prompt_title)
        }

        override fun description(): Optional<String> {
            return Optional.empty()
        }

        override fun cancelButtonText(): String {
            return getString(R.string.verify_biometric_prompt_cancel_button_title)
        }
    }
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

        viewModel.updateViewModel(navigationArguments.parameter, biometricPromptOptions)

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
            viewModel.updateViewModel(parameter, biometricPromptOptions)
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