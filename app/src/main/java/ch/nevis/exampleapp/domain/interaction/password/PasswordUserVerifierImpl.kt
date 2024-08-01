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
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerifier
import timber.log.Timber

/**
 * Default implementation of [PasswordUserVerifier] interface. Navigates to Credential view with the
 * received [PasswordUserVerificationHandler], [ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus]
 * and [ch.nevis.mobile.sdk.api.operation.userverification.PasswordUserVerificationError] objects.
 */
class PasswordUserVerifierImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : PasswordUserVerifier {

    //region PasswordUserVerifier
    override fun verifyPassword(
        context: PasswordUserVerificationContext,
        handler: PasswordUserVerificationHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("Password user verification failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start Password user verification.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PasswordNavigationParameter(
                    CredentialViewMode.VERIFICATION,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    passwordAuthenticatorProtectionStatus = context.authenticatorProtectionStatus(),
                    passwordUserVerificationHandler = handler
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during Password verification.")
    }
    //endregion
}
