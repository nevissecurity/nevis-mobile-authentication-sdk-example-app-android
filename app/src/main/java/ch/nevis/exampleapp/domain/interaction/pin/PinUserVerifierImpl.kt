/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction.pin

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.credential.model.CredentialViewMode
import ch.nevis.exampleapp.ui.credential.parameter.PinNavigationParameter
import ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import timber.log.Timber

/**
 * Default implementation of [PinUserVerifier] interface. Navigates to Credential view with the received
 * [PinUserVerificationHandler] and the [PinAuthenticatorProtectionStatus] objects.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class PinUserVerifierImpl(
    private val navigationDispatcher: NavigationDispatcher
) : PinUserVerifier {

    //region PinUserVerifier
    /** @suppress */
    override fun verifyPin(
        context: PinUserVerificationContext,
        handler: PinUserVerificationHandler
    ) {
        Timber.asTree().sdk("Please start PIN user verification.")

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PinNavigationParameter(
                    CredentialViewMode.VERIFICATION,
                    pinAuthenticatorProtectionStatus = context.authenticatorProtectionStatus(),
                    pinUserVerificationHandler = handler
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during PIN verification.")
    }
    //endregion
}
