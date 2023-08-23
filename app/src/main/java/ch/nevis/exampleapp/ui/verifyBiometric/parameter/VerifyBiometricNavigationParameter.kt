/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyBiometric.parameter

import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.verifyBiometric.model.VerifyBiometricViewMode
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationError
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter data class for Verify User view.
 */
@Parcelize
data class VerifyBiometricNavigationParameter(
    /**
     * The mode, the Verify User view intend to be used/initialized.
     */
    val verifyBiometricViewMode: VerifyBiometricViewMode,

    /**
     * An instance of a [BiometricUserVerificationHandler] in case of an operation is started that requested
     * biometric user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using biometric authentication (fingerprint or face ID) to be able to continue the operation.
     *
     * [VerifyBiometricNavigationParameter.verifyBiometricViewMode] must be [VerifyBiometricViewMode.BIOMETRIC].
     */
    @IgnoredOnParcel
    val biometricUserVerificationHandler: BiometricUserVerificationHandler? = null,

    /**
     * An instance of a [DevicePasscodeUserVerificationHandler] in case of an operation is started that requested
     * device passcode user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using device passcode authentication to be able to continue the operation.
     *
     * [VerifyBiometricNavigationParameter.verifyBiometricViewMode] must be [VerifyBiometricViewMode.DEVICE_PASSCODE].
     */
    @IgnoredOnParcel
    val devicePasscodeUserVerificationHandler: DevicePasscodeUserVerificationHandler? = null,

    /**
     * An instance of a [FingerprintUserVerificationHandler] in case of an operation is started that requested
     * fingerprint user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using fingerprint authentication to be able to continue the operation.
     *
     * [VerifyBiometricNavigationParameter.verifyBiometricViewMode] must be [VerifyBiometricViewMode.FINGERPRINT].
     */
    @IgnoredOnParcel
    val fingerprintUserVerificationHandler: FingerprintUserVerificationHandler? = null,

    /**
     * A [FingerprintUserVerificationError] error object that may occur during fingerprint user verification.
     *
     * [VerifyBiometricNavigationParameter.verifyBiometricViewMode] must be [VerifyBiometricViewMode.FINGERPRINT].
     */
    @IgnoredOnParcel
    val fingerprintUserVerificationError: FingerprintUserVerificationError? = null
) : NavigationParameter
