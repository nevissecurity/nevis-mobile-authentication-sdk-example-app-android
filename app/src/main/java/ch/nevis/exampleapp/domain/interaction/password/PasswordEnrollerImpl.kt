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
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnroller
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentContext
import ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentHandler
import ch.nevis.mobile.sdk.api.operation.password.PasswordPolicy
import timber.log.Timber

/**
 * Default implementation of [PasswordEnroller] interface. Navigates to Credential view with the
 * received [PasswordEnrollmentHandler] and [ch.nevis.mobile.sdk.api.operation.password.PasswordEnrollmentError]
 * objects.
 *
 * @constructor Creates a new instance.
 * @param policy An instance of a [PasswordPolicy] interface implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class PasswordEnrollerImpl(
    private val policy: PasswordPolicy,
    private val navigationDispatcher: NavigationDispatcher
) : PasswordEnroller {

    //region PasswordEnroller
    /** @suppress */
    override fun enrollPassword(
        context: PasswordEnrollmentContext,
        handler: PasswordEnrollmentHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("Password enrollment failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start Password enrollment.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PasswordNavigationParameter(
                    CredentialViewMode.ENROLLMENT,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    passwordEnrollmentHandler = handler
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during Password enrollment.")
    }

    //  You can add custom password policy by overriding the `passwordPolicy` getter
    /** @suppress */
    override fun passwordPolicy(): PasswordPolicy = policy
    //endregion
}
