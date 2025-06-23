/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction.password

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.exampleapp.ui.credential.parameter.PasswordNavigationParameter
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerifier
import timber.log.Timber

/**
 * Default implementation of [PasswordUserVerifier] interface. Navigates to Credential view with the
 * received [PasswordUserVerificationHandler] and the [PasswordAuthenticatorProtectionStatus] objects.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class PasswordUserVerifierImpl(
    private val navigationDispatcher: NavigationDispatcher
) : PasswordUserVerifier {

    //region PasswordUserVerifier
    /** @suppress */
    override fun verifyPassword(
        context: PasswordUserVerificationContext,
        handler: PasswordUserVerificationHandler
    ) {
        Timber.asTree().sdk("Please start password user verification.")

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PasswordNavigationParameter(
                    CredentialViewMode.VERIFICATION,
                    passwordAuthenticatorProtectionStatus = context.authenticatorProtectionStatus(),
                    passwordUserVerificationHandler = handler
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during Password verification.")
    }
    //endregion
}
