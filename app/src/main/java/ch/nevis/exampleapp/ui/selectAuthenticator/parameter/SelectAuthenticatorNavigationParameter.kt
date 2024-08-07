/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.ui.selectAuthenticator.parameter

import ch.nevis.exampleapp.ui.base.model.NavigationParameter
import ch.nevis.exampleapp.ui.selectAuthenticator.model.AuthenticatorItem
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Navigation parameter data class for select authenticator view.
 *
 * @constructor Creates a new instance.
 * @param authenticatorItems The list of available authenticator items the user can select from.
 * @param authenticatorSelectionHandler An instance of an [AuthenticatorSelectionHandler] implementation.
 */
@Parcelize
data class SelectAuthenticatorNavigationParameter(
    /**
     * The list of available authenticator items the user can select from.
     */
    @IgnoredOnParcel
    val authenticatorItems: Set<AuthenticatorItem>? = null,

    /**
     * An instance of an [AuthenticatorSelectionHandler] in case an operation started that need authentication selection
     * and we navigate to Select Authenticator view to ask the user to select one of the available authenticators to be able
     * to continue the operation.
     */
    @IgnoredOnParcel
    val authenticatorSelectionHandler: AuthenticatorSelectionHandler? = null
) : NavigationParameter
