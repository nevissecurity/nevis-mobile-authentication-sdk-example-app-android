/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.pin.parameter

import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.pin.model.PinViewMode
import ch.nevis.mobile.sdk.api.operation.RecoverableError
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter data class for PIN view.
 */
@Parcelize
data class PinNavigationParameter(
    /**
     * The mode, the PIN view intend to be used/initialized.
     */
    val pinViewMode: PinViewMode,

    /**
     * Status object of the PIN authenticator.
     */
    @IgnoredOnParcel
    val pinAuthenticatorProtectionStatus: PinAuthenticatorProtectionStatus? = null,

    /**
     * The last recoverable error. It exists only if there was already a failed PIN operation attempt.
     */
    @IgnoredOnParcel
    val lastRecoverableError: RecoverableError? = null,

    /**
     * An instance of a [PinChangeHandler] in case a PIN change operation is started and we navigate to
     * PIN view to ask the user to enter the old and new PINs to be able to continue the operation.
     * [PinNavigationParameter.pinViewMode] must be [PinViewMode.CHANGE_PIN].
     */
    @IgnoredOnParcel
    val pinChangeHandler: PinChangeHandler? = null,

    /**
     * An instance of a [PinEnrollmentHandler] in case a PIN enrollment is started as part of a
     * registration operation and we navigate to PIN view to ask the user to enter, define the PIN to
     * be able to continue the operation.
     * [PinNavigationParameter.pinViewMode] must be [PinViewMode.ENROLL_PIN].
     */
    @IgnoredOnParcel
    val pinEnrollmentHandler: PinEnrollmentHandler? = null,

    /**
     * An instance of a [PinUserVerificationHandler] in case a PIN verification is started as part of
     * an authentication operation and we navigate to PIN view to ask the user to enter the PIN to be able
     * to continue the operation.
     * [PinNavigationParameter.pinViewMode] must be [PinViewMode.VERIFY_PIN].
     */
    @IgnoredOnParcel
    val pinUserVerificationHandler: PinUserVerificationHandler? = null
) : NavigationParameter
