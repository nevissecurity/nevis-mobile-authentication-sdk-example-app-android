/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023-2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential

import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.databinding.FragmentCredentialBinding
import ch.nevis.exampleapp.domain.model.sdk.PasswordAuthenticatorProtectionStatusLastAttemptFailedImpl
import ch.nevis.exampleapp.domain.model.sdk.PinAuthenticatorProtectionStatusLastAttemptFailedImpl
import ch.nevis.exampleapp.domain.util.message
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.credential.model.CredentialViewData
import ch.nevis.exampleapp.ui.credential.parameter.CredentialNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of Credential view where the user
 * can enroll, change and verify credential (PIN or password).
 *
 * @constructor Creates a new instance.
 */
@AndroidEntryPoint
class CredentialFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentCredentialBinding? = null
    private val binding get() = _binding!!

    /**
     * View model implementation of this view.
     */
    override val viewModel: CredentialViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: CredentialFragmentArgs by navArgs()

    /**
     * A [CountDownTimer] instance that is used to disable the screen for a cool down time period if necessary.
     */
    private var timer: CountDownTimer? = null
    //endregion

    //region Fragment
    /** @suppress */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCredentialBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** @suppress */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateViewModel(navigationArguments.parameter)

        binding.confirmButton.setOnClickListener {
            viewModel.confirm(
                binding.oldCredentialTextInputEditText.text?.toList()?.toCharArray() ?: charArrayOf(),
                binding.credentialTextInputEditText.text?.toList()?.toCharArray() ?: charArrayOf()
            )
        }

        binding.oldCredentialTextInputEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            clearErrors()
        })

        binding.credentialTextInputEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            clearErrors()
        })

        // Override back button handling.
        val callback = object : CancelOperationOnBackPressedCallback(viewModel) {
            override fun handleOnBackPressed() {
                timer?.cancel()
                timer = null
                super.handleOnBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /** @suppress */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is CredentialNavigationParameter) {
            viewModel.updateViewModel(parameter)
            return true
        }
        return false
    }

    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)

        val credentialViewData = viewData as? CredentialViewData ?: return

        binding.titleTextView.setText(credentialViewData.title)
        binding.descriptionTextView.setText(credentialViewData.description)

        updateTextViews(credentialViewData)

        binding.messageTextView.visibility = View.GONE
        credentialViewData.protectionInformation?.let { protectionInformation ->
            protectionInformation.message.let {
                binding.messageTextView.visibility = View.VISIBLE
                binding.messageTextView.text = it
            }
            setViewState(!protectionInformation.isLocked)
            protectionInformation.remainingRetries?.let { remainingRetries ->
                    updateMessage(
                        credentialViewData.credentialType,
                        remainingRetries,
                        protectionInformation.coolDownTime
                    )
                if (protectionInformation.coolDownTime > 0) {
                    startCoolDownTimer(
                        credentialViewData.credentialType,
                        remainingRetries,
                        protectionInformation.coolDownTime
                    )
                }
            }
        }
    }
    //endregion

    //region Private Interface

    /**
     * Updates the text views based on the view data.
     *
     * @param credentialViewData The view data.
     */
    private fun updateTextViews(credentialViewData: CredentialViewData) {
        binding.oldCredentialTextInputLayout.setHint(credentialViewData.oldCredentialTextFieldHint)
        binding.oldCredentialTextInputLayout.visibility = credentialViewData.oldCredentialVisibility
        binding.credentialTextInputLayout.setHint(credentialViewData.credentialTextFieldHint)
        when (credentialViewData.credentialType) {
            Authenticator.PIN_AUTHENTICATOR_AAID -> {
                binding.oldCredentialTextInputEditText.inputType = (InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                binding.credentialTextInputEditText.inputType = (InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD)
            }
            Authenticator.PASSWORD_AUTHENTICATOR_AAID -> {
                binding.oldCredentialTextInputEditText.inputType =
                    (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                binding.credentialTextInputEditText.inputType = (InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD)
            }
        }

        credentialViewData.lastRecoverableError?.let { lastRecoverableError ->
            var errorMessage = lastRecoverableError.description()
            if (lastRecoverableError.cause().isPresent) {
                errorMessage += " " + lastRecoverableError.cause().get().message

            }

            binding.credentialTextInputLayout.error = errorMessage
        }
    }

    /**
     * Clears all error messages on screen.
     */
    private fun clearErrors() {
        binding.oldCredentialTextInputLayout.error = ""
        binding.credentialTextInputLayout.error = ""
    }

    /**
     * Enables or disables the UI components of this view.
     *
     * @param isEnabled A flag that tells whether the UI components should be enabled.
     */
    private fun setViewState(isEnabled: Boolean) {
        binding.oldCredentialTextInputLayout.isEnabled = isEnabled
        binding.credentialTextInputLayout.isEnabled = isEnabled
        binding.confirmButton.isEnabled = isEnabled
    }

    /**
     * Starts the cool down timer.
     *
     * @param credentialType The type of the actual credential.
     * @param remainingRetries The number of remaining retries available.
     * @param coolDownTime The time that must be passed before the user can try to provide credentials again.
     */
    private fun startCoolDownTimer(credentialType: String, remainingRetries: Int, coolDownTime: Long) {
        if (timer != null) {
            return
        }

        timer = object : CountDownTimer(
            coolDownTime * 1000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                updateMessage(credentialType, remainingRetries, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                setViewState(true)
                updateMessage(credentialType, remainingRetries, 0)
                timer = null
            }
        }.start()
    }

    /**
     * Updates the text of message text view.
     *
     * @param credentialType The type of the actual credential.
     * @param remainingRetries The number of remaining retries available.
     * @param coolDownTime The time that must be passed before the user can try to provide credentials again.
     */
    private fun updateMessage(credentialType: String, remainingRetries: Int, coolDownTime: Long) {
        binding.messageTextView.visibility = View.VISIBLE
        binding.messageTextView.text = context?.let {
            when (credentialType) {
                Authenticator.PIN_AUTHENTICATOR_AAID ->
                    PinAuthenticatorProtectionStatusLastAttemptFailedImpl(
                        remainingRetries = remainingRetries,
                        coolDownTimeInSeconds = coolDownTime
                    ).message(it)
                Authenticator.PASSWORD_AUTHENTICATOR_AAID ->
                    PasswordAuthenticatorProtectionStatusLastAttemptFailedImpl(
                        remainingRetries = remainingRetries,
                        coolDownTimeInSeconds = coolDownTime
                    ).message(it)
                else -> String()
            }
        }
    }
    //endregion
}
