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
import ch.nevis.mobile.sdk.api.operation.pin.PinEnroller
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentContext
import ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentHandler
import timber.log.Timber

/**
 * Default implementation of [PinEnroller] interface. Navigates to Credential view with the received
 * [PinEnrollmentHandler] and [ch.nevis.mobile.sdk.api.operation.pin.PinEnrollmentError] objects.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class PinEnrollerImpl(
    private val navigationDispatcher: NavigationDispatcher
) : PinEnroller {

    //region PinEnroller
    /** @suppress */
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
            NavigationGraphDirections.actionGlobalCredentialFragment(
                PinNavigationParameter(
                    CredentialViewMode.ENROLLMENT,
                    lastRecoverableError = context.lastRecoverableError().orElse(null),
                    pinEnrollmentHandler = handler
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree().sdk("Valid credentials provided during PIN enrollment.")
    }
    //endregion
}
