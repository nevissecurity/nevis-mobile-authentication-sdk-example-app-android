/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.verifyBiometric.model.VerifyBiometricViewMode
import ch.nevis.exampleapp.ui.verifyBiometric.parameter.VerifyBiometricNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import timber.log.Timber

/**
 * Default implementation of [BiometricUserVerifier] interface. It navigates to the Verify Biometric
 * view with the received [BiometricUserVerificationHandler] object.
 */
class BiometricUserVerifierImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : BiometricUserVerifier {

    //region BiometricUserVerifier
    override fun verifyBiometric(
        biometricUserVerificationContext: BiometricUserVerificationContext,
        biometricUserVerificationHandler: BiometricUserVerificationHandler
    ) {
        Timber.asTree().sdk("Verify yourself using biometric authenticator.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyBiometricFragment(
                VerifyBiometricNavigationParameter(
                    VerifyBiometricViewMode.BIOMETRIC,
                    biometricUserVerificationHandler = biometricUserVerificationHandler
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree()
            .sdk("Valid credentials provided during biometric verification.")
    }
    //endregion
}