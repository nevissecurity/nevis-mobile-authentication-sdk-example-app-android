/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.credential.parameter

import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.mobile.sdk.api.operation.RecoverableError
import ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.password.PasswordChangeHandler
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter of the Credential view in case of Password authenticator.
 */
@Parcelize
data class PasswordNavigationParameter(
    /**
     * The mode, the Credential view intend to be used/initialized.
     */
    override val credentialViewMode: CredentialViewMode,

    /**
     * The last recoverable error. It exists only if there was already a failed Password operation attempt.
     */
    @IgnoredOnParcel
    override val lastRecoverableError: RecoverableError? = null,

    /**
     * Status object of the Password authenticator.
     */
    @IgnoredOnParcel
    val passwordAuthenticatorProtectionStatus: PasswordAuthenticatorProtectionStatus? = null,

    /**
     * An instance of a [PasswordChangeHandler] in case a Password change operation is started and we
     * navigate to Credential view to ask the user to enter the old and new passwords to be able to
     * continue the operation.
     * [PasswordNavigationParameter.credentialViewMode] must be [CredentialViewMode.CHANGE].
     */
    @IgnoredOnParcel
    val passwordChangeHandler: PasswordChangeHandler? = null,

    /**
     * An instance of a [PasswordEnrollmentHandler] in case a Password enrollment is started as part
     * of a registration operation and we navigate to Credential view to ask the user to enter, define
     * the password to be able to continue the operation.
     * [PasswordNavigationParameter.credentialViewMode] must be [CredentialViewMode.ENROLLMENT].
     */
    @IgnoredOnParcel
    val passwordEnrollmentHandler: PasswordEnrollmentHandler? = null,

    /**
     * An instance of a [PasswordUserVerificationHandler] in case a Password verification is started
     * as part of an authentication operation and we navigate to Credential view to ask the user to
     * enter the password to be able to continue the operation.
     * [PasswordNavigationParameter.credentialViewMode] must be [CredentialViewMode.VERIFICATION].
     */
    @IgnoredOnParcel
    val passwordUserVerificationHandler: PasswordUserVerificationHandler? = null
) : CredentialNavigationParameter()
