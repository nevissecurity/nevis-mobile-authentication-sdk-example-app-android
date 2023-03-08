/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.pin

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ch.nevis.exampleapp.R
import ch.nevis.exampleapp.databinding.FragmentPinBinding
import ch.nevis.exampleapp.ui.base.BaseFragment
import ch.nevis.exampleapp.ui.base.CancelOperationOnBackPressedCallback
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.base.model.ViewData
import ch.nevis.exampleapp.ui.pin.model.PinViewData
import ch.nevis.exampleapp.ui.pin.parameter.PinNavigationParameter
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * [androidx.fragment.app.Fragment] implementation of PIN view where the user
 * can enroll, change and verify PIN.
 */
@AndroidEntryPoint
class PinFragment : BaseFragment() {

    //region Properties
    /**
     * UI component bindings.
     */
    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!

    /**
     * View model implementation of this view.
     */
    override val viewModel: PinViewModel by viewModels()

    /**
     * Safe Args navigation arguments.
     */
    private val navigationArguments: PinFragmentArgs by navArgs()

    /**
     * A [CountDownTimer] instance that is used to disable the screen for a cool down time period if necessary.
     */
    private var timer: CountDownTimer? = null

    /**
     * A boolean flag that tells whether the timer is started/running or not.
     */
    private var isTimerRunning = false
    //endregion

    //region Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateViewModel(navigationArguments.parameter)

        binding.confirmButton.setOnClickListener {
            viewModel.processPins(
                binding.oldPinTextInputEditText.text?.toList()?.toCharArray() ?: charArrayOf(),
                binding.pinTextInputEditText.text?.toList()?.toCharArray() ?: charArrayOf()
            )
        }

        // Set on text changed listeners.
        binding.oldPinTextInputEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            clearErrors()
        })

        binding.pinTextInputEditText.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            clearErrors()
        })

        // Override back button handling.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            CancelOperationOnBackPressedCallback(viewModel)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region BaseFragment
    override fun updateViewParameter(parameter: NavigationParameter): Boolean {
        if (parameter is PinNavigationParameter) {
            viewModel.updateViewModel(parameter)
            return true
        }
        return false
    }

    override fun updateView(viewData: ViewData) {
        super.updateView(viewData)

        val pinViewData = viewData as? PinViewData ?: return

        binding.titleTextView.setText(pinViewData.title)
        binding.messageTextView.setText(pinViewData.message)
        binding.oldPinTextInputLayout.visibility = pinViewData.oldPinVisibility
        binding.pinTextInputLayout.setHint(pinViewData.pinTextFieldHint)

        pinViewData.lastRecoverableError?.let {
            binding.pinTextInputLayout.error = it.description()
        }

        when (pinViewData.pinAuthenticatorProtectionStatus) {
            // In case of cool down state the screen should be disabled for the specified cool down time period.
            is PinAuthenticatorProtectionStatus.LastAttemptFailed -> {
                setViewState(
                    false,
                    coolDownTime = pinViewData.pinAuthenticatorProtectionStatus.coolDownTimeInSeconds()
                )
                isTimerRunning = true
                timer = object : CountDownTimer(
                    pinViewData.pinAuthenticatorProtectionStatus.coolDownTimeInSeconds() * 1000,
                    1000
                ) {
                    override fun onTick(millisUntilFinished: Long) {
                        binding.messageTextView.text = context?.getString(
                            R.string.pin_message_cool_down,
                            millisUntilFinished / 1000
                        )
                    }

                    override fun onFinish() {
                        setViewState(true, pinViewData.message)
                        isTimerRunning = false
                    }
                }.start()
            }
            // In case of locked state, the screen will be disabled forever.
            is PinAuthenticatorProtectionStatus.LockedOut -> {
                setViewState(false)
            }
            else -> {
                // Do nothing.
            }
        }
    }
    //endregion

    //region Private Interface
    /**
     * Clears all error messages on screen.
     */
    private fun clearErrors() {
        binding.oldPinTextInputLayout.error = ""
        binding.pinTextInputLayout.error = ""
    }

    /**
     * Enables or disables UI components of this view.
     *
     * @param isEnabled A flag that tells the UI components should be enabled or disabled.
     * @param message String resource identifier that will be used as message text in case the view enabled.
     * Message text will be an empty string if this parameter is null. Message text will be overridden with error,
     * warning messages in case the view is disabled.
     * @param coolDownTime The cool down time period the UI components are disabled for. Please note this method
     * won't start a timer, this value is used only for information text composing.
     */
    private fun setViewState(
        isEnabled: Boolean,
        message: Int? = null,
        coolDownTime: Long? = null
    ) {
        binding.oldPinTextInputLayout.isEnabled = isEnabled
        binding.pinTextInputLayout.isEnabled = isEnabled
        binding.confirmButton.isEnabled = isEnabled
        if (isEnabled) {
            message?.also {
                binding.messageTextView.setText(it)
            } ?: run { binding.messageTextView.text = "" }
        } else {
            binding.messageTextView.text = if (coolDownTime != null) {
                context?.getString(R.string.pin_message_cool_down, coolDownTime)
            } else {
                context?.getString(R.string.pin_message_locked_out)
            }
        }
    }
    //endregion
}