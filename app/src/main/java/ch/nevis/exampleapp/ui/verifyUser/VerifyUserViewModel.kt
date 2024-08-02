/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyUser

import ch.nevis.exampleapp.common.error.ErrorHandler
import ch.nevis.exampleapp.domain.model.error.BusinessException
import ch.nevis.exampleapp.ui.base.CancellableOperationViewModel
import ch.nevis.exampleapp.ui.verifyUser.model.VerifyUserViewData
import ch.nevis.exampleapp.ui.verifyUser.model.VerifyUserViewMode
import ch.nevis.exampleapp.ui.verifyUser.parameter.VerifyUserNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricPromptOptions
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodePromptOptions
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.OsAuthenticationListenHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model implementation of Verify User view.
 *
 * @constructor Creates a new instance.
 * @param errorHandler An instance of an [ErrorHandler] interface implementation.
 */
@HiltViewModel
class VerifyUserViewModel @Inject constructor(
    private val errorHandler: ErrorHandler
) : CancellableOperationViewModel() {

    //region Properties
    /**
     * The mode, the Verify User view intend to be used/initialized.
     */
    private var verifyUserViewMode: VerifyUserViewMode? = null

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

    /**
     * An instance of a [BiometricUserVerificationHandler] in case of an operation is started that requested
     * biometric user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using biometric authentication (fingerprint or face ID) to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.BIOMETRIC].
     */
    private var biometricUserVerificationHandler: BiometricUserVerificationHandler? = null

    /**
     * An instance of a [DevicePasscodeUserVerificationHandler] in case of an operation is started that requested
     * device passcode user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using device passcode authentication to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.DEVICE_PASSCODE].
     */
    private var devicePasscodeUserVerificationHandler: DevicePasscodeUserVerificationHandler? = null

    /**
     * An instance of a [FingerprintUserVerificationHandler] in case of an operation is started that requested
     * fingerprint user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using fingerprint authentication to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.FINGERPRINT].
     */
    private var fingerprintUserVerificationHandler: FingerprintUserVerificationHandler? = null

    /**
     * The [OsAuthenticationListenHandler] that is received when [BiometricUserVerificationHandler.listenForOsCredentials] or
     * [FingerprintUserVerificationHandler.listenForOsCredentials] is called based on the current mode of [VerifyUserFragment].
     */
    private var osAuthenticationListenHandler: OsAuthenticationListenHandler? = null
    //endregion

    //region Public Interface
    /**
     * Updates this view model instance based on the [VerifyUserNavigationParameter] that was received by
     * the owner [VerifyUserFragment]. This method must be called by the owner fragment.
     *
     * @param parameter The [VerifyUserNavigationParameter] that was received by the owner [VerifyUserFragment].
     * @param biometricPromptOptions [BiometricPromptOptions] object that is required in case of biometric user
     * verification for the dialog shown by the OS.
     * @param devicePasscodePromptOptions [DevicePasscodePromptOptions] object that is required in case of device
     * passcode user verification for the dialog shown by the OS.
     */
    fun updateViewModel(
        parameter: VerifyUserNavigationParameter,
        biometricPromptOptions: BiometricPromptOptions,
        devicePasscodePromptOptions: DevicePasscodePromptOptions
    ) {
        biometricUserVerificationHandler = null
        fingerprintUserVerificationHandler = null
        devicePasscodeUserVerificationHandler = null
        verifyUserViewMode = parameter.verifyUserViewMode
        this.biometricPromptOptions = biometricPromptOptions
        this.devicePasscodePromptOptions = devicePasscodePromptOptions
        biometricUserVerificationHandler = parameter.biometricUserVerificationHandler
        devicePasscodeUserVerificationHandler = parameter.devicePasscodeUserVerificationHandler
        fingerprintUserVerificationHandler = parameter.fingerprintUserVerificationHandler

        parameter.fingerprintUserVerificationError?.let {
            requestViewUpdate(VerifyUserViewData(it.description()))
        }
    }

    /**
     * Resumes listening for OS credentials.
     */
    fun resumeOsListening() {
        osAuthenticationListenHandler = osAuthenticationListenHandler?.resumeListening()
    }

    /**
     * Pauses listening for OS credentials.
     */
    fun pauseOsListening() {
        osAuthenticationListenHandler = osAuthenticationListenHandler?.pauseListening()
    }

    /**
     * Verifies the user using the previously selected authentication method.
     */
    fun verifyUser() {
        when (verifyUserViewMode) {
            VerifyUserViewMode.FINGERPRINT -> verifyFingerprint()
            VerifyUserViewMode.BIOMETRIC -> verifyBiometric()
            VerifyUserViewMode.DEVICE_PASSCODE -> verifyDevicePasscode()
            else -> throw BusinessException.invalidState()
        }
    }
    //endregion

    //region Private Interface
    /**
     * Starts fingerprint authentication for an operation.
     */
    private fun verifyFingerprint() {
        try {
            requestViewUpdate(VerifyUserViewData(null, true))

            val fingerprintUserVerificationHandler =
                this.fingerprintUserVerificationHandler ?: throw BusinessException.invalidState()
            osAuthenticationListenHandler =
                fingerprintUserVerificationHandler.listenForOsCredentials()
            this.fingerprintUserVerificationHandler = null
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Starts biometric authentication for an operation.
     */
    private fun verifyBiometric() {
        try {
            val biometricUserVerificationHandler =
                this.biometricUserVerificationHandler ?: throw BusinessException.invalidState()
            osAuthenticationListenHandler =
                biometricUserVerificationHandler.listenForOsCredentials(biometricPromptOptions)
            this.biometricUserVerificationHandler = null
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }

    /**
     * Starts device passcode authentication for an operation.
     */
    private fun verifyDevicePasscode() {
        try {
            val devicePasscodeUserVerificationHandler =
                this.devicePasscodeUserVerificationHandler ?: throw BusinessException.invalidState()
            osAuthenticationListenHandler =
                devicePasscodeUserVerificationHandler.listenForOsCredentials(
                    devicePasscodePromptOptions
                )
            this.devicePasscodeUserVerificationHandler = null
        } catch (exception: Exception) {
            errorHandler.handle(exception)
        }
    }
    //endregion

    //region CancellableOperationViewModel
    override fun cancelOperation() {
        osAuthenticationListenHandler?.cancelAuthentication()
        osAuthenticationListenHandler = null

        fingerprintUserVerificationHandler?.cancel()
        fingerprintUserVerificationHandler = null

        biometricUserVerificationHandler?.cancel()
        biometricUserVerificationHandler = null
    }
    //endregion
}
