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
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeContext
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinChanger
import timber.log.Timber

/**
 * Default implementation of [PinChanger] interface. Navigates to Credential view with the received
 * [PinChangeHandler], [ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus] and
 * [ch.nevis.mobile.sdk.api.operation.pin.PinChangeRecoverableError] objects.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class PinChangerImpl(
    private val navigationDispatcher: NavigationDispatcher
) : PinChanger {

    //region PinChanger
    /** @suppress */
    override fun changePin(
        context: PinChangeContext,
        handler: PinChangeHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("PIN change failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start PIN change.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PinNavigationParameter(
                    CredentialViewMode.CHANGE,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    pinAuthenticatorProtectionStatus = context.authenticatorProtectionStatus(),
                    pinChangeHandler = handler
                )
            )
        )
    }
    //endregion
}
