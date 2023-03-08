/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.pin

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.pin.model.PinViewData
import ch.nevis.exampleapp.ui.pin.model.PinViewMode
import ch.nevis.exampleapp.ui.pin.parameter.PinNavigationParameter
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model implementation of PIN view.
 */
@HiltViewModel
class PinViewModel @Inject constructor(

    /**
     * An instance of an [ErrorHandler] interface implementation. Received errors will be passed to this error
     * handler instance.
     */
    private val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    /**
     * An instance of a [PinChangeHandler] in case a PIN change operation is started and we navigate to
     * PIN view to ask the user to enter the old and new PINs to be able to continue the operation.
     * [PinViewModel.pinViewMode] must be [PinViewMode.CHANGE_PIN].
     */
    private var pinChangeHandler: PinChangeHandler? = null

    /**
     * An instance of a [PinEnrollmentHandler] in case a PIN enrollment is started as part of a
     * registration operation and we navigate to PIN view to ask the user to enter, define the PIN to
     * be able to continue the operation.
     * [PinViewModel.pinViewMode] must be [PinViewMode.ENROLL_PIN].
     */
    private var pinEnrollmentHandler: PinEnrollmentHandler? = null

    /**
     * An instance of a [PinUserVerificationHandler] in case a PIN verification is started as part of
     * an authentication operation and we navigate to PIN view to ask the user to enter the PIN to be able
     * to continue the operation.
     * [PinViewModel.pinViewMode] must be [PinViewMode.VERIFY_PIN].
     */
    private var pinUserVerificationHandler: PinUserVerificationHandler? = null

    /**
     * The mode, the PIN view intend to be used/initialized.
     */
    private var pinViewMode: PinViewMode? = null

    //region Public Interface
    /**
     * Updates this view model instance based on the [PinNavigationParameter] that was received by
     * the owner [PinFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [PinNavigationParameter] that was received by the owner [PinFragment].
     */
    fun updateViewModel(parameter: PinNavigationParameter) {
        pinViewMode = parameter.pinViewMode
        pinChangeHandler = parameter.pinChangeHandler
        pinEnrollmentHandler = parameter.pinEnrollmentHandler
        pinUserVerificationHandler = parameter.pinUserVerificationHandler
        val pinViewData = PinViewData(
            parameter.pinViewMode,
            parameter.pinAuthenticatorProtectionStatus,
            parameter.lastRecoverableError
        )
        requestViewUpdate(pinViewData)
    }

    /**
     * Processes the given PINs based on the current [PinViewMode].
     *
     * @param oldPin The text entered into old PIN text field. It is only used when the current [PinViewMode]
     * is [PinViewMode.CHANGE_PIN] otherwise it will be ignored.
     * @param pin The text entered into PIN text field.
     */
    fun processPins(oldPin: CharArray, pin: CharArray) {
        try {
            when (pinViewMode) {
                PinViewMode.CHANGE_PIN -> changePin(oldPin, pin)
                PinViewMode.ENROLL_PIN -> enrollPin(pin)
                PinViewMode.VERIFY_PIN -> verifyPin(pin)
                else -> throw BusinessException.invalidState()
            }
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region Private Interface
    /**
     * Changes the previously enrolled PIN.
     *
     * @param oldPin The old PIN to be verified before changing it.
     * @param newPin The new PIN.
     */
    private fun changePin(oldPin: CharArray, newPin: CharArray) {
        val pinChangeHandler =
            this.pinChangeHandler ?: throw BusinessException.invalidState()
        pinChangeHandler.pins(oldPin, newPin)
        this.pinChangeHandler = null
    }

    /**
     * Enrolls a new PIN during a registration operation.
     *
     * @param pin The PIN entered by the user.
     */
    private fun enrollPin(pin: CharArray) {
        val pinEnrollmentHandler =
            this.pinEnrollmentHandler ?: throw BusinessException.invalidState()
        pinEnrollmentHandler.pin(pin)
        this.pinEnrollmentHandler = null
    }

    /**
     * Verifies the given PIN during a authentication operation.
     *
     * @param pin The PIN entered by the user.
     */
    private fun verifyPin(pin: CharArray) {
        val pinUserVerificationHandler =
            this.pinUserVerificationHandler ?: throw BusinessException.invalidState()
        pinUserVerificationHandler.verifyPin(pin)
        this.pinUserVerificationHandler = null
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        pinChangeHandler?.cancel()
        pinChangeHandler = null

        pinEnrollmentHandler?.cancel()
        pinEnrollmentHandler = null

        pinUserVerificationHandler?.cancel()
        pinUserVerificationHandler = null
    }
    //endregion
}