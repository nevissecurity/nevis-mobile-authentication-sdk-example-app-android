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
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationContext
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerificationHandler
import ch.nevis.mobile.sdk.api.operation.userverification.DevicePasscodeUserVerifier
import timber.log.Timber

/**
 * Default implementation of [DevicePasscodeUserVerifier] interface. It navigates to the Verify User
 * view with the received [DevicePasscodeUserVerificationHandler] object.
 */
class DevicePasscodeUserVerifierImpl(

    /**
     * An instance of a [NavigationDispatcher] interface implementation.
     */
    private val navigationDispatcher: NavigationDispatcher
) : DevicePasscodeUserVerifier {

    //region DevicePasscodeUserVerifier
    override fun verifyDevicePasscode(
        devicePasscodeUserVerificationContext: DevicePasscodeUserVerificationContext,
        devicePasscodeUserVerificationHandler: DevicePasscodeUserVerificationHandler
    ) {
        Timber.asTree().sdk("Verify yourself using device passcode authenticator.")
        navigationDispatcher.requestNavigation(
            NavigationGraphDirections.actionGlobalVerifyBiometricFragment(
                VerifyBiometricNavigationParameter(
                    VerifyBiometricViewMode.DEVICE_PASSCODE,
                    devicePasscodeUserVerificationHandler = devicePasscodeUserVerificationHandler
                )
            )
        )
    }

    override fun onValidCredentialsProvided() {
        Timber.asTree()
            .sdk("Valid credentials provided during device passcode verification.")
    }
    //endregion
}