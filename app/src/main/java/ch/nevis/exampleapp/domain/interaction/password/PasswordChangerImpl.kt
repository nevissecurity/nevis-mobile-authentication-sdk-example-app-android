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
import ch.nevis.mobile.sdk.api.operation.password.PasswordChangeContext
import ch.nevis.mobile.sdk.api.operation.password.PasswordChangeHandler
import ch.nevis.mobile.sdk.api.operation.password.PasswordChanger
import ch.nevis.mobile.sdk.api.operation.password.PasswordPolicy
import timber.log.Timber

/**
 * Default implementation of [PasswordChanger] interface. Navigates to Credential view with the received
 * [PasswordChangeHandler], [ch.nevis.mobile.sdk.api.operation.password.PasswordAuthenticatorProtectionStatus]
 * and [ch.nevis.mobile.sdk.api.operation.password.PasswordChangeRecoverableError] objects.
 */
class PasswordChangerImpl(
    /**
     * An instance of a [PasswordPolicy] interface implementation.
     */
    private val policy: PasswordPolicy,

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : PasswordChanger {

    //region PasswordChanger
    override fun changePassword(
        context: PasswordChangeContext,
        handler: PasswordChangeHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("Password change failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start Password change.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PasswordNavigationParameter(
                    CredentialViewMode.CHANGE,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    passwordAuthenticatorProtectionStatus = context.authenticatorProtectionStatus(),
                    passwordChangeHandler = handler
                )
            )
        )
    }

    //  You can add custom password policy by overriding the `passwordPolicy` getter
    override fun passwordPolicy(): PasswordPolicy = policy
    //endregion
}
