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
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeContext
import ch.nevis.mobile.sdk.api.operation.pin.PinChangeHandler
import ch.nevis.mobile.sdk.api.operation.pin.PinChanger
import timber.log.Timber

/**
 * Default implementation of [PinChanger] interface. It navigates to PIN view with the received
 * [PinChangeHandler], [ch.nevis.mobile.sdk.api.operation.pin.PinAuthenticatorProtectionStatus] and
 * [ch.nevis.mobile.sdk.api.operation.pin.PinChangeRecoverableError] objects.
 */
class PinChangerImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : PinChanger {
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
            NavigationGraphDirections.actionGlobalPinFragment(
                PinNavigationParameter(
                    PinViewMode.CHANGE_PIN,
                    context.authenticatorProtectionStatus(),
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    pinChangeHandler = handler
                )
            )
        )
    }
}
