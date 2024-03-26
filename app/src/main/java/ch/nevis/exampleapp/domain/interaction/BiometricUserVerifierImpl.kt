/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.domain.interaction

import ch.nevis.exampleapp.NavigationGraphDirections
import ch.nevis.exampleapp.domain.util.titleResId
import ch.nevis.exampleapp.logging.sdk
import ch.nevis.exampleapp.ui.navigation.NavigationDispatcher
import ch.nevis.exampleapp.ui.verifyUser.model.VerifyUserViewMode
import ch.nevis.exampleapp.ui.verifyUser.parameter.VerifyUserNavigationParameter
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.BiometricUserVerifier
import timber.log.Timber

/**
 * Default implementation of [BiometricUserVerifier] interface. It navigates to the Verify User
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
        Timber.asTree().sdk("Please start biometric user verification.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyUserFragment(
                VerifyUserNavigationParameter(
                    VerifyUserViewMode.BIOMETRIC,
                    biometricUserVerificationContext.authenticator().titleResId(),
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