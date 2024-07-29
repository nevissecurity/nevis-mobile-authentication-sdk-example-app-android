/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential.parameter

import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.mobile.sdk.api.operation.RecoverableError
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter of the Credential view in case of PIN authenticator.
 */
@Parcelize
data class PinNavigationParameter(
    /**
     * The mode, the Credential view intend to be used/initialized.
     */
    override val credentialViewMode: CredentialViewMode,

    /**
     * The last recoverable error. It exists only if there was already a failed PIN operation attempt.
     */
    @IgnoredOnParcel
    override val lastRecoverableError: RecoverableError? = null,

    /**
     * Status object of the PIN authenticator.
     */
    @IgnoredOnParcel
    val pinAuthenticatorProtectionStatus: PinAuthenticatorProtectionStatus? = null,

    /**
     * An instance of a [PinChangeHandler] in case a PIN change operation is started and we navigate
     * to Credential view to ask the user to enter the old and new PINs to be able to continue the
     * operation. [PinNavigationParameter.credentialViewMode] must be [CredentialViewMode.CHANGE].
     */
    @IgnoredOnParcel
    val pinChangeHandler: PinChangeHandler? = null,

    /**
     * An instance of a [PinEnrollmentHandler] in case a PIN enrollment is started as part of a
     * registration operation and we navigate to Credential view to ask the user to enter, define the
     * PIN to be able to continue the operation.
     * [PinNavigationParameter.credentialViewMode] must be [CredentialViewMode.ENROLLMENT].
     */
    @IgnoredOnParcel
    val pinEnrollmentHandler: PinEnrollmentHandler? = null,

    /**
     * An instance of a [PinUserVerificationHandler] in case a PIN verification is started as part of
     * an authentication operation and we navigate to Credential view to ask the user to enter the PIN
     * to be able to continue the operation.
     * [PinNavigationParameter.credentialViewMode] must be [CredentialViewMode.VERIFICATION].
     */
    @IgnoredOnParcel
    val pinUserVerificationHandler: PinUserVerificationHandler? = null
) : CredentialNavigationParameter()
