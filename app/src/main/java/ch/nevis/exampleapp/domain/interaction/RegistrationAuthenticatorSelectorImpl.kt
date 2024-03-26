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
import ch.nevis.exampleapp.domain.util.titleResId
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.selectAuthenticator.model.AuthenticatorItem
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
    private val navigationDispatcher: NavigationDispatcher,

    /**
     * An instance of a [Settings] interface implementation.
     */
    private val settings: Settings
) : RegistrationAuthenticatorSelector {

    //region RegistrationAuthenticatorSelector
    override fun selectAuthenticator(
        authenticatorSelectionContext: AuthenticatorSelectionContext,
        authenticatorSelectionHandler: AuthenticatorSelectionHandler
    ) {
        Timber.asTree()
            .sdk("Please select one of the received available authenticators!")

        val authenticatorItems = authenticatorSelectionContext.authenticators().mapNotNull {
            mapForRegistration(it, authenticatorSelectionContext)
        }.toSet()

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalSelectAuthenticatorFragment(
                SelectAuthenticatorNavigationParameter(
                    Operation.REGISTRATION,
                    authenticatorItems,
                    authenticatorSelectionHandler
                )
            )
        )
    }
    //endregion

    //region Private Interface
    private fun mapForRegistration(
        authenticator: Authenticator,
        context: AuthenticatorSelectionContext
    ): AuthenticatorItem? {
        Timber.d("Checking if authenticator %s is eligible for registration.", authenticator.aaid())
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
            return null
        }

        // Do not display policy non-compliant authenticators (this includes already registered
        // authenticators), nor those not supported by hardware.
        return if (authenticator.isSupportedByHardware && context.isPolicyCompliant(authenticator.aaid())) {
            AuthenticatorItem(
                authenticator.aaid(),
                true,
                authenticator.isUserEnrolled(
                    context.account().username(),
                    settings.allowClass2Sensors
                ),
                authenticator.titleResId()
            )
        } else {
            null
        }
    }
    //endregion
}