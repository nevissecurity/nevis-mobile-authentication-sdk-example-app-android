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
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import timber.log.Timber

/**
 * Default implementation of [FingerprintUserVerifier] interface. It navigates to the Verify User
 * view with the received [FingerprintUserVerificationHandler] object.
 */
class FingerprintUserVerifierImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : FingerprintUserVerifier {

    //region FingerprintUserVerifier
    override fun verifyFingerprint(
        fingerprintUserVerificationContext: FingerprintUserVerificationContext,
        fingerprintUserVerificationHandler: FingerprintUserVerificationHandler
    ) {
        Timber.asTree().sdk("Verify yourself using fingerprint authenticator.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyBiometricFragment(
                VerifyBiometricNavigationParameter(
                    VerifyBiometricViewMode.FINGERPRINT,
                    fingerprintUserVerificationHandler = fingerprintUserVerificationHandler,
                    fingerprintUserVerificationError = fingerprintUserVerificationContext.lastRecoverableError()
                        .orElse(null)
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree()
            .sdk("Valid credentials provided during fingerprint verification.")
    }
    //endregion
}