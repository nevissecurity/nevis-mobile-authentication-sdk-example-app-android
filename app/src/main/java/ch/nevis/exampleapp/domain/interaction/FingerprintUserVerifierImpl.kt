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
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.FingerprintUserVerifier
import timber.log.Timber

/**
 * Default implementation of [FingerprintUserVerifier] interface. It navigates to the Verify User
 * view with the received [FingerprintUserVerificationHandler] object.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class FingerprintUserVerifierImpl(
    private val navigationDispatcher: NavigationDispatcher
) : FingerprintUserVerifier {

    //region FingerprintUserVerifier
    /** @suppress */
    override fun verifyFingerprint(
        context: FingerprintUserVerificationContext,
        handler: FingerprintUserVerificationHandler
    ) {
        if (context.lastRecoverableError().isPresent) {
            Timber.asTree().sdk("Fingerprint user verification failed. Please try again.")
        } else {
            Timber.asTree().sdk("Please start fingerprint user verification.")
        }

        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyUserFragment(
                VerifyUserNavigationParameter(
                    VerifyUserViewMode.FINGERPRINT,
                    context.authenticator().titleResId(),
                    fingerprintUserVerificationHandler = handler,
                    fingerprintUserVerificationError = context.lastRecoverableError()
                        .orElse(null)
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree()
            .sdk("Valid credentials provided during fingerprint verification.")
    }
    //endregion
}
