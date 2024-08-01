/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2024. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.common.configuration.ConfigurationProvider
import ch.nevis.exampleapp.common.settings.Settings
import ch.nevis.exampleapp.domain.util.isUserEnrolled
import ch.nevis.exampleapp.domain.util.titleResId
import ch.nevis.exampleapp.domain.validation.AuthenticatorValidator
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAuthenticator.model.AuthenticatorItem
import ch.nevis.exampleapp.ui.selectAuthenticator.parameter.SelectAuthenticatorNavigationParameter
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionContext
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionHandler
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelector
import timber.log.Timber

/**
 * Supported operations during authenticator selection.
 */
enum class AuthenticatorSelectorOperation {

    /**
     * Registration operation.
     */
    REGISTRATION,

    /**
     * Authentication operation.
     */
    AUTHENTICATION,
}

/**
 * Default implementation of [AuthenticatorSelector] interface.
 *
 * @constructor Creates a new instance.
 * @param configurationProvider An instance of a [ConfigurationProvider] interface implementation.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 * @param authenticatorValidator An instance of a [AuthenticatorValidator] interface implementation.
 * @param settings An instance of a [Settings] interface implementation.
 * @param operation The current operation.
 */
class AuthenticatorSelectorImpl(
    private val configurationProvider: ConfigurationProvider,
    private val navigationDispatcher: NavigationDispatcher,
    private val authenticatorValidator: AuthenticatorValidator,
    private val settings: Settings,
    private val operation: AuthenticatorSelectorOperation
) : AuthenticatorSelector {

    //region AuthenticatorSelector
    /** @suppress */
    override fun selectAuthenticator(
        context: AuthenticatorSelectionContext,
        handler: AuthenticatorSelectionHandler
    ) {
        Timber.asTree().sdk("Please select one of the received available authenticators!")

        val authenticators = when (operation) {
            AuthenticatorSelectorOperation.REGISTRATION -> {
                authenticatorValidator.validateForRegistration(
                    context,
                    configurationProvider.authenticatorAllowlist
                )
            }
            AuthenticatorSelectorOperation.AUTHENTICATION -> {
                authenticatorValidator.validateForAuthentication(
                    context,
                    configurationProvider.authenticatorAllowlist
                )
            }
        }

        if (authenticators.isEmpty()) {
            Timber.e("No available authenticators found. Cancelling authenticator selection.")
            return handler.cancel()
        }

        val authenticatorItems = authenticators.map {
            AuthenticatorItem(
                it.aaid(),
                context.isPolicyCompliant(it.aaid()),
                it.isUserEnrolled(
                    context.account().username(),
                    settings.allowClass2Sensors
                ),
                it.titleResId()
            )
        }.toSet()

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalSelectAuthenticatorFragment(
                SelectAuthenticatorNavigationParameter(
                    authenticatorItems,
                    handler
                )
            )
        )
    }
    //endregion
}
