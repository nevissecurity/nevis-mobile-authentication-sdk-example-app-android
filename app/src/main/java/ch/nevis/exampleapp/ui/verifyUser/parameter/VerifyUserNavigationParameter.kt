/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.verifyUser.parameter

import androidx.annotation.StringRes
import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.verifyUser.model.VerifyUserViewMode
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationError
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter data class for Verify User view.
 *
 * @constructor Creates a new instance.
 * @param verifyUserViewMode The mode, the Verify User view intend to be used/initialized.
 * @param authenticatorTitleResId String resource identifier of the title of the authenticator.
 * @param biometricUserVerificationHandler An instance of a [BiometricUserVerificationHandler] implementation.
 * @param devicePasscodeUserVerificationHandler An instance of a [DevicePasscodeUserVerificationHandler] implementation.
 * @param fingerprintUserVerificationHandler An instance of a [FingerprintUserVerificationHandler] implementation.
 * @param fingerprintUserVerificationError A [FingerprintUserVerificationError] error object that may
 *      occur during fingerprint user verification.
 */
@Parcelize
data class VerifyUserNavigationParameter(
    /**
     * The mode, the Verify User view intend to be used/initialized.
     */
    val verifyUserViewMode: VerifyUserViewMode,

    /**
     * String resource identifier of the title of the authenticator.
     */
    @StringRes
    val authenticatorTitleResId: Int,

    /**
     * An instance of a [BiometricUserVerificationHandler] in case of an operation is started that requested
     * biometric user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using biometric authentication (fingerprint or face ID) to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.BIOMETRIC].
     */
    @IgnoredOnParcel
    val biometricUserVerificationHandler: BiometricUserVerificationHandler? = null,

    /**
     * An instance of a [DevicePasscodeUserVerificationHandler] in case of an operation is started that requested
     * device passcode user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using device passcode authentication to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.DEVICE_PASSCODE].
     */
    @IgnoredOnParcel
    val devicePasscodeUserVerificationHandler: DevicePasscodeUserVerificationHandler? = null,

    /**
     * An instance of a [FingerprintUserVerificationHandler] in case of an operation is started that requested
     * fingerprint user verification and we navigate to Verify User view to ask the user to verify herself/himself
     * using fingerprint authentication to be able to continue the operation.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.FINGERPRINT].
     */
    @IgnoredOnParcel
    val fingerprintUserVerificationHandler: FingerprintUserVerificationHandler? = null,

    /**
     * A [FingerprintUserVerificationError] error object that may occur during fingerprint user verification.
     *
     * [VerifyUserNavigationParameter.verifyUserViewMode] must be [VerifyUserViewMode.FINGERPRINT].
     */
    @IgnoredOnParcel
    val fingerprintUserVerificationError: FingerprintUserVerificationError? = null
) : NavigationParameter
