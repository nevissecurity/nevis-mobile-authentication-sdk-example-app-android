/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAuthenticator.parameter.SelectAuthenticatorNavigationParameter
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionContext
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionHandler
import timber.log.Timber

/**
 * Default implementation of [AuthenticationAuthenticatorSelector] interface. It collects registered
 * and hardware supported authenticators for an authentication operation and navigates to Select
 * Authenticator view.
 */
class AuthenticationAuthenticatorSelectorImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : AuthenticationAuthenticatorSelector {

    //region AuthenticatorSelector
    override fun selectAuthenticator(
        authenticatorSelectionContext: AuthenticatorSelectionContext,
        authenticatorSelectionHandler: AuthenticatorSelectionHandler
    ) {
        Timber.asTree()
            .sdk("Please select one of the received available authenticators!")

        val authenticators = authenticatorSelectionContext.authenticators().filter {
            it.registration().isRegistered(authenticatorSelectionContext.account().username()) &&
                    it.isSupportedByHardware
        }.toSet()

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalSelectAuthenticatorFragment(
                SelectAuthenticatorNavigationParameter(
                    Operation.AUTHENTICATION,
                    authenticators,
                    authenticatorSelectionHandler
                )
            )
        )
    }
    //endregion
}
