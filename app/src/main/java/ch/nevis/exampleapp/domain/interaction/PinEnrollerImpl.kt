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
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentContext
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import timber.log.Timber

/**
 * Default implementation of [PinEnroller] interface. It navigates to PIN view with the received
 * [PinEnrollmentHandler] and [ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentError] objects.
 */
class PinEnrollerImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : PinEnroller {

    //region PinEnroller
    override fun enrollPin(
        context: PinEnrollmentContext,
        handler: PinEnrollmentHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("PIN enrollment failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start PIN enrollment.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalPinFragment(
                PinNavigationParameter(
                    PinViewMode.ENROLL_PIN,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    pinEnrollmentHandler = handler
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during PIN enrollment.")
    }
    //endregion
}
