/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.pin.model.PinViewMode
import ch.nevis.exampleapp.ui.pin.parameter.PinNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerifier
import timber.log.Timber

/**
 * Default implementation of [PinUserVerifier] interface. It navigates to PIN view with the received
 * [PinUserVerificationHandler], [ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus] and
 * [ch.nevis.mobile.sdk.api.operation.userverification.PinUserVerificationError] objects.
 */
class PinUserVerifierImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : PinUserVerifier {

    //region PinUserVerifier
    override fun verifyPin(
        pinUserVerificationContext: PinUserVerificationContext,
        pinUserVerificationHandler: PinUserVerificationHandler
    ) {
        Timber.asTree().sdk("Verify yourself using PIN authenticator.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalPinFragment(
                PinNavigationParameter(
                    PinViewMode.VERIFY_PIN,
                    pinAuthenticatorProtectionStatus = pinUserVerificationContext.authenticatorProtectionStatus(),
                    lastRecoverableError = pinUserVerificationContext.lastRecoverableError()
                        .orElse(null),
                    pinUserVerificationHandler = pinUserVerificationHandler
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during PIN verification.")
    }
    //endregion
}
