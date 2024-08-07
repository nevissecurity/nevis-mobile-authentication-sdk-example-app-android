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
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import timber.log.Timber

/**
 * Default implementation of [DevicePasscodeUserVerifier] interface. It navigates to the Verify User
 * view with the received [DevicePasscodeUserVerificationHandler] object.
 *
 * @constructor Creates a new instance.
 * @param navigationDispatcher An instance of a [NavigationDispatcher] interface implementation.
 */
class DevicePasscodeUserVerifierImpl(
    private val navigationDispatcher: NavigationDispatcher
) : DevicePasscodeUserVerifier {

    //region DevicePasscodeUserVerifier
    /** @suppress */
    override fun verifyDevicePasscode(
        context: DevicePasscodeUserVerificationContext,
        handler: DevicePasscodeUserVerificationHandler
    ) {
        Timber.asTree().sdk("Please start device passcode user verification.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyUserFragment(
                VerifyUserNavigationParameter(
                    VerifyUserViewMode.DEVICE_PASSCODE,
                    context.authenticator().titleResId(),
                    devicePasscodeUserVerificationHandler = handler
                )
            )
        )
    }

    /** @suppress */
    override fun onValidCredentialsProvided() {
        Timber.asTree()
            .sdk("Valid credentials provided during device passcode verification.")
    }
    //endregion
}
