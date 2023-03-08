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
import ch.nevis.mobile.sdk.api.localdata.Authenticator
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionContext
import ch.nevis.mobile.sdk.api.operation.selection.AuthenticatorSelectionHandler
import timber.log.Timber

/**
 * Default implementation of [RegistrationAuthenticatorSelector] interface. It collects non-registered,
 * hardware supported, policy compliant authenticators for an registration operation and navigates to Select
 * Authenticator view.
 */
class RegistrationAuthenticatorSelectorImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : RegistrationAuthenticatorSelector {

    //region AuthenticatorSelector
    override fun selectAuthenticator(
        authenticatorSelectionContext: AuthenticatorSelectionContext,
        authenticatorSelectionHandler: AuthenticatorSelectionHandler
    ) {
        Timber.asTree()
            .sdk("Please select one of the received available authenticators!")

        val authenticators = authenticatorSelectionContext.authenticators().filter {
            isAvailableForRegistration(it, authenticatorSelectionContext)
        }.toSet()

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalSelectAuthenticatorFragment(
                SelectAuthenticatorNavigationParameter(
                    Operation.REGISTRATION,
                    authenticators,
                    authenticatorSelectionHandler
                )
            )
        )
    }
    //endregion

    //region Private Interface
    private fun isAvailableForRegistration(
        authenticator: Authenticator,
        context: AuthenticatorSelectionContext
    ): Boolean {
        val username = context.account().username()
        val authenticators = context.authenticators()

        val biometricRegistered = authenticators.any {
            it.aaid() == Authenticator.BIOMETRIC_AUTHENTICATOR_AAID &&
                    it.registration().isRegistered(username)
        }

        val canRegisterBiometric = authenticators.any {
            it.aaid() == Authenticator.BIOMETRIC_AUTHENTICATOR_AAID &&
                    context.isPolicyCompliant(it.aaid()) &&
                    it.isSupportedByHardware
        }

        val canRegisterFingerprint = authenticators.any {
            it.aaid() == Authenticator.FINGERPRINT_AUTHENTICATOR_AAID &&
                    context.isPolicyCompliant(it.aaid()) &&
                    it.isSupportedByHardware
        }

        // If biometric can be registered (or is already registered), or if we cannot
        // register fingerprint, do not propose to register fingerprint (we favor biometric over fingerprint).
        if ((canRegisterBiometric || biometricRegistered || !canRegisterFingerprint) &&
            authenticator.aaid() == Authenticator.FINGERPRINT_AUTHENTICATOR_AAID
        ) {
            return false
        }

        // Do not display policy non-compliant authenticators (this includes already registered
        // authenticators), nor those not supported by hardware.
        return authenticator.isSupportedByHardware && context.isPolicyCompliant(authenticator.aaid())
    }
    //endregion
}