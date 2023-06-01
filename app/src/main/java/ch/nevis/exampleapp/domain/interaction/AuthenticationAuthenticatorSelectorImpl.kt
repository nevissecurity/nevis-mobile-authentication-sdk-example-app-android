/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.domain.model.operation.Operation
import ch.nevis.exampleapp.domain.util.isUserEnrolled
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAuthenticator.model.AuthenticatorItem
import ch.nevis.exampleapp.ui.selectAuthenticator.parameter.SelectAuthenticatorNavigationParameter
import ch.nevis.mobile.sdk.api.localdata.Authenticator
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
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of a [Settings] interface implementation.
     */
    private val settings: Settings
) : AuthenticationAuthenticatorSelector {

    //region AuthenticatorSelector
    override fun selectAuthenticator(
        authenticatorSelectionContext: AuthenticatorSelectionContext,
        authenticatorSelectionHandler: AuthenticatorSelectionHandler
    ) {
        Timber.asTree()
            .sdk("Please select one of the received available authenticators!")

        val authenticatorItems = authenticatorSelectionContext.authenticators().mapNotNull {
            mapForAuthentication(it, authenticatorSelectionContext)
        }.toSet()

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalSelectAuthenticatorFragment(
                SelectAuthenticatorNavigationParameter(
                    Operation.AUTHENTICATION,
                    authenticatorItems,
                    authenticatorSelectionHandler
                )
            )
        )
    }
    //endregion

    //region Private Interface
    private fun mapForAuthentication(
        authenticator: Authenticator,
        context: AuthenticatorSelectionContext
    ): AuthenticatorItem? {
        return if (authenticator.registration()
                .isRegistered(context.account().username()) && authenticator.isSupportedByHardware
        ) {
            AuthenticatorItem(
                authenticator.aaid(),
                context.isPolicyCompliant(authenticator.aaid()),
                authenticator.isUserEnrolled(
                    context.account().username(),
                    settings.allowClass2Sensors
                )
            )
        } else {
            null
        }
    }
    //endregion
}
